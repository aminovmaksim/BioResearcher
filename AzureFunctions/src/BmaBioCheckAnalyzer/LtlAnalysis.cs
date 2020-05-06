// Copyright (c) Microsoft Research 2016
// License: MIT. See LICENSE
using BioCheckAnalyzerCommon;
using BioModelAnalyzer;
using bma.Diagnostics;
using Microsoft.FSharp.Core;
using System;
using System.Diagnostics;
using System.Linq;

namespace bma.LTL
{
    public static class Analysis
    {
        public static Tuple<LTLAnalysisResult, LTLAnalysisResult> Polarity(LTLPolarityAnalysisInputDTO input)
        {
            LogService log = new LogService();

            try
            {
                string formula = input.Formula;
                string num_of_steps = input.Number_of_steps;
                FSharpOption<bool> polarity = FSharpOption<bool>.None;
                if (input.Polarity != LTLStatus.Unknown)
                    polarity = new FSharpOption<bool>(input.Polarity == LTLStatus.True);

                IAnalyzer analyzer = new UIMain.Analyzer();
                if (input.EnableLogging)
                {
                    analyzer.LoggingOn(log);
                }
                else
                {
                    analyzer.LoggingOff();
                    log.LogDebug("Logging is disabled.");
                }

                Stopwatch sw = new Stopwatch();
                sw.Start();
                var result = analyzer.checkLTLPolarity(input, formula, num_of_steps, polarity);
                sw.Stop();
                log.LogDebug(string.Format("The LTL polarity check took {0}", sw.Elapsed));

                var positive = new LTLAnalysisResult
                {
                    Error = result.Item1.Error,
                    Ticks = result.Item1.Ticks,
                    Status = result.Item1.Status ? LTLStatus.True : LTLStatus.False,
                    Loop = result.Item1.Loop,
                    ErrorMessages = log.ErrorMessages.Length > 0 ? log.ErrorMessages.ToArray() : null,
                    DebugMessages = log.DebugMessages.Length > 0 ? log.DebugMessages.ToArray() : null
                };

                LTLAnalysisResult negative = null;
                if (result.Item2 != null && !FSharpOption<LTLAnalysisResultDTO>.get_IsNone(result.Item2))
                {
                    negative = new LTLAnalysisResult
                    {
                        Error = result.Item2.Value.Error,
                        Ticks = result.Item2.Value.Ticks,
                        Status = result.Item2.Value.Status ? LTLStatus.True : LTLStatus.False,
                        Loop = result.Item2.Value.Loop,
                        ErrorMessages = log.ErrorMessages.Length > 0 ? log.ErrorMessages.ToArray() : null,
                        DebugMessages = log.DebugMessages.Length > 0 ? log.DebugMessages.ToArray() : null
                    };
                }

                return new Tuple<LTLAnalysisResult, LTLAnalysisResult>(positive, negative);
            }
            catch (Exception ex)
            {
                var version = typeof(Analysis).Assembly.GetName().Version;
                log.LogError(String.Format("LTL Polarity check failed. Assembly version: {0}. Exception: {1}", version, ex));
                return new Tuple<LTLAnalysisResult, LTLAnalysisResult>(new LTLAnalysisResult
                {
                    Error = ex.Message,
                    ErrorMessages = log.ErrorMessages.Length > 0 ? log.ErrorMessages.ToArray() : null,
                    DebugMessages = log.DebugMessages.Length > 0 ? log.DebugMessages.ToArray() : null
                }, null);
            }
        }

        public static LTLAnalysisResult Simulate(LTLSimulationAnalysisInputDTO input)
        {
            LogService log = new LogService();

            try
            {
                string formula = input.Formula;
                string num_of_steps = input.Number_of_steps;
              
                IAnalyzer analyzer = new UIMain.Analyzer();
                if (input.EnableLogging)
                {
                    analyzer.LoggingOn(log);
                }
                else
                {
                    analyzer.LoggingOff();
                    log.LogDebug("Logging is disabled.");
                }

                Stopwatch sw = new Stopwatch();
                sw.Start();
                var result = analyzer.checkLTLSimulation(input, formula, num_of_steps);
                sw.Stop();
                log.LogDebug(string.Format("The LTL simulation took {0}", sw.Elapsed));

                if (FSharpOption<LTLAnalysisResultDTO>.get_IsNone(result))
                {
                    return new LTLAnalysisResult
                    {
                        Error = null,
                        Ticks = null,
                        Status = LTLStatus.Unknown,
                        Loop = -1,
                        ErrorMessages = log.ErrorMessages.Length > 0 ? log.ErrorMessages.ToArray() : null,
                        DebugMessages = log.DebugMessages.Length > 0 ? log.DebugMessages.ToArray() : null
                    };
                }
                else
                {
                    var res = result.Value;
                    var status = res.Status;
                    return new LTLAnalysisResult
                    {
                        Error = res.Error,
                        Ticks = res.Ticks,
                        Status = status ? LTLStatus.True : LTLStatus.False,
                        Loop = res.Loop,
                        ErrorMessages = log.ErrorMessages.Length > 0 ? log.ErrorMessages.ToArray() : null,
                        DebugMessages = log.DebugMessages.Length > 0 ? log.DebugMessages.ToArray() : null
                    };
                }
            }
            catch (Exception ex)
            {
                var version = typeof(Analysis).Assembly.GetName().Version;
                log.LogError(String.Format("LTL simulation failed. Assembly version: {0}. Exception: {1}", version, ex));
                return new LTLAnalysisResult
                {
                    Error = ex.Message,
                    ErrorMessages = log.ErrorMessages.Length > 0 ? log.ErrorMessages.ToArray() : null,
                    DebugMessages = log.DebugMessages.Length > 0 ? log.DebugMessages.ToArray() : null
                };
            }
        }
    }
}
