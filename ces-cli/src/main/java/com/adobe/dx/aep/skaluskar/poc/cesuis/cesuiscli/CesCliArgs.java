/**
 * CesCliArgs.java
 */
package com.adobe.dx.aep.skaluskar.poc.cesuis.cesuiscli;

import com.github.jankroken.commandline.annotations.LongSwitch;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.Required;
import com.github.jankroken.commandline.annotations.ShortSwitch;
import com.github.jankroken.commandline.annotations.SingleArgument;
import com.github.jankroken.commandline.annotations.Toggle;

import lombok.Getter;

/**
 * @author skaluskar
 *
 */
@Getter
public class CesCliArgs
{
  public static final String USAGE =
      "ces.sh\n"
          + "\t-(input|i) <path to file containing spec>\n"
          + "\t-(output|o) <path to dir to save output>\n"
          + "\t[-(randomize|r)]\n"
          + "\t[-(thread)|t]\n"
          + "\t[-(prefix|p)] <prefix of thread orders to include>\n"
          + "\t[-(seed|s) <string to derive seed for randomization>]\n"
          + "\t[-(max|m) <max runs to simulate>]\n";

  /** path to the spec describing operations and number of threads */
  private String             inputPath;

  /** path to a directory where reports will be saved */
  private String             outputDirPath;

  /** run each thread at a time serially */
  private boolean            threadAtATime;

  private String            execSteps;

  /** prefix of thread orders to include */
  private String             prefix;

  /** if true, then random runs will be simulated instead of all runs */
  private boolean            randomizeRuns;

  /** seed for randomization */
  private String             seed;

  /** max number of runs to simulate */
  private int                maxRuns;

  /**
   * @param dest
   *          the inputPath to set
   */
  @Option
  @LongSwitch("input")
  @ShortSwitch("i")
  @SingleArgument
  @Required
  public void setInputPath(String path)
  {
    inputPath = path;
  }

  /**
   * @param p
   *          the prefix to set
   */
  @Option
  @LongSwitch("prefix")
  @ShortSwitch("p")
  @SingleArgument
  public void setPrefix(String p)
  {
    prefix = p;
  }

  /**
   * @param dir
   *          the outputDirPath to set
   */
  @Option
  @LongSwitch("output")
  @ShortSwitch("o")
  @SingleArgument
  @Required
  public void setOutputDirPath(String dir)
  {
    outputDirPath = dir;
  }

  /**
   * @param max
   *          the maxRuns to set
   */
  @Option
  @LongSwitch("randomize")
  @ShortSwitch("r")
  @Toggle(value = true)
  public void setRandomizeRuns(boolean r)
  {
    randomizeRuns = r;
  }

  /**
   * @param s
   *          the seed to set
   */
  @Option
  @LongSwitch("seed")
  @ShortSwitch("s")
  @SingleArgument
  public void setSeed(String s)
  {
    seed = s;
  }

  /**
   * @param max
   *          the maxRuns to set
   */
  @Option
  @LongSwitch("max")
  @ShortSwitch("m")
  @SingleArgument
  public void setMaxRuns(String maxStr)
  {
    maxRuns = Integer.parseInt(maxStr);
  }

  /**
   * @param t
   *          the threadAtATime to set
   */
  @Option
  @LongSwitch("thread")
  @ShortSwitch("t")
  @Toggle(value = true)
  public void setThreadAtATime(boolean t)
  {
    threadAtATime = t;
  }

}
