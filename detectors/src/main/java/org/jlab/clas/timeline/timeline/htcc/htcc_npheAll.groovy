package org.jlab.clas.timeline.timeline.htcc
import java.util.concurrent.ConcurrentHashMap
import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.clas.timeline.fitter.HTCCFitter

class htcc_npheAll {

  def data = new ConcurrentHashMap()

  def processDirectory(dir, run) {
    def h1 = dir.getObject('/HTCC/npheAll')
    def f1 = HTCCFitter.timeAllFit(h1)
    def mean = f1.getParameter(1)
    def sigma = f1.getParameter(2)
    data[run] = [run:run, h1:h1 ]
  }



  def close() {

    TDirectory out = new TDirectory()
    out.mkdir('/timelines')
    // (0..<6).each{ sec->
    def grtl = new GraphErrors('htcc_npheAll_mean')
    grtl.setTitle("Mean Combined HTCC timing")
    grtl.setTitleY("Mean Combined HTCC timing (ns)")
    grtl.setTitleX("run number")

    data.sort{it.key}.each{run,it->
      out.mkdir('/'+it.run)
      out.cd('/'+it.run)
      out.addDataSet(it.h1)
      grtl.addPoint(it.run, it.h1.getMean(), 0, 0)
    }
    out.cd('/timelines')
    out.addDataSet(grtl)

    out.writeFile('htcc_npheAll.hipo')
  }
}
