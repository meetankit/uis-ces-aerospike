/**
 * CesUISCli.java
 */
package com.adobe.dx.aep.skaluskar.poc.cesuis.cesuiscli;

import java.lang.reflect.InvocationTargetException;

import com.adobe.dx.aep.skaluskar.poc.ces.driver.CesDriver;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.ExceptionUtils;
import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.github.jankroken.commandline.domain.InvalidCommandLineException;
import com.github.jankroken.commandline.domain.InvalidOptionConfigurationException;
import com.github.jankroken.commandline.domain.UnrecognizedSwitchException;

/**
 * @author skaluskar
 *
 */
public class CesUISCli
{
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    CesCliArgs cliArgs = null;

    try
    {
      cliArgs = CommandLineParser.parse(CesCliArgs.class, args,
          OptionStyle.SIMPLE);
    }
    catch (InvalidOptionConfigurationException | IllegalAccessException
        | InstantiationException | InvocationTargetException e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }
    catch (UnrecognizedSwitchException e)
    {
      System.err.println(String.format("Invalid option: %s\nExpected: %s",
          e.getMessage(), CesCliArgs.USAGE));
      System.exit(1);
    }
    catch (InvalidCommandLineException e)
    {
      System.err.println(String.format("Missing arguments\nExpected: %s",
          CesCliArgs.USAGE));
      System.exit(1);
    }
    try
    {
      CesDriver driver = new CesDriver();
      driver.setInputPath(cliArgs.getInputPath());
      driver.setOutputDirPath(cliArgs.getOutputDirPath());
      driver.setPrefix(cliArgs.getPrefix());
      driver.setRandomizeRuns(cliArgs.isRandomizeRuns());
      driver.setThreadAtATime(cliArgs.isThreadAtATime());
      driver.setSeed(cliArgs.getSeed());
      driver.setMaxRuns(cliArgs.getMaxRuns());
      driver.run();
    }
    catch (Exception e)
    {
      Throwable ue = ExceptionUtils.userReportableError(e);
      System.err.println("Error: " + ue.getMessage());
      System.exit(1);
    }
  }
}
