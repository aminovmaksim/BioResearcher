using BioModelAnalyzer;
using bma.BioCheck;
using Newtonsoft.Json;
using System;
using System.Collections;
using System.Collections.Generic;
using static azure_fun.TestRequest;
using static BioModelAnalyzer.Model;

namespace azure_fun
{
	class TestingUtil
	{
		public static TestResponse Run(SingleTestRequest test)
		{

            AnalysisInput input = new AnalysisInput
            {
                ModelName = test.Model.ModelName,
                Variables = test.Model.Variables,
                Relationships = test.Model.Relationships,
                EnableLogging = true
			};

            TestResponse testResponse = runTest(test.test, test.Model);
			return testResponse;
		}

        public static TestResponse runTest(Test test, Model model)
        {
            TestResponse testResponse = new TestResponse();
            testResponse.Name = test.Name;
            testResponse.Id = test.Id;

            List<SimulationVariable> req_vars = new List<SimulationVariable>();

            Console.WriteLine(JsonConvert.SerializeObject(model));

            if (test.Act.Type.Equals("block"))
            {
                foreach (Variable variable in model.Variables)
                {
                    SimulationVariable tmp = new SimulationVariable
                    {
                        Id = variable.Id,
                        Value = 0
                    };
                    req_vars.Add(tmp);
                }

                for (int i = 0; (i < 100); i++)
                {
                    bool ready = processTest(ref req_vars, ref model, ref test);

                    if (ready)
                    {
                        AnalysisInput input = new AnalysisInput
                        {
                            ModelName = model.ModelName,
                            Variables = model.Variables,
                            Relationships = model.Relationships,
                            EnableLogging = true
                        };

                        AnalysisResult result = Analysis.Analyze(input);

                        Console.WriteLine(test.Name + "===== analyze result ===== " + result.Status.ToString());
                        Console.WriteLine(test.Name + "==================== isStab : " + isStabilizing(result));


                        if (isStabilizing(result) == test.ExpectStab)
                        {
                            testResponse.TestSuccess = true;
                        } else
                        {
                            testResponse.TestSuccess = false;
                        }
                        return testResponse;
                    }

                    SimulationInput simulationInput = new SimulationInput
                    {
                        Model = model,
                        Variables = req_vars.ToArray()
                    };
                    SimulationVariable[] simulationResult = Simulation.Simulate(simulationInput).Variables;
                    req_vars = new List<SimulationVariable>(simulationResult);
                }

            }
            else
            {
                foreach (Variable variable in model.Variables)
                {
                    SimulationVariable tmp = new SimulationVariable
                    {
                        Id = variable.Id,
                        Value = 0
                    };
                    req_vars.Add(tmp);
                }

                for (int i = 0; (i < 100); i++)
                {
                    bool ready = processTest(ref req_vars, ref model, ref test);
                    if (ready)
                    {
                        testResponse.TestSuccess = true;
                        return testResponse;
                    }
                    SimulationInput simulationInput = new SimulationInput
                    {
                        Model = model,
                        Variables = req_vars.ToArray()
                    };
                    SimulationVariable[] simulationResult = Simulation.Simulate(simulationInput).Variables;
                    req_vars = new List<SimulationVariable>(simulationResult);
                }

            }

            testResponse.SyntaxError = "Cannot get test result, maybe condition never executes";
            return testResponse;
        }

        public static bool processTest(ref List<SimulationVariable> req_vars, ref Model model, ref Test test)
        {

            if (test.Act.Type.Equals("block"))
            {

                foreach (SimulationVariable var in req_vars)
                {
                    if (test.Cond.Var.Equals(getNameById(var.Id, model)))
                    {

                        Variable variable = model.Variables[getVariableIndex(test.Act.Var, model)];
                        int variable_value = req_vars[getSimulationVariableIndex(req_vars, variable.Id)].Value;


                        if (test.Cond.Operation.Equals("=="))
                        {
                            if (test.Cond.Value.Equals(var.Value))
                            {


                                model.Variables[getVariableIndex(test.Act.Var, model)].Formula
                                    = variable_value.ToString();
                                return true;
                            }
                        }
                        if (test.Cond.Operation.Equals(">"))
                        {
                            if (test.Cond.Value > var.Value)
                            {
                                model.Variables[getVariableIndex(test.Act.Var, model)].Formula
                                    = variable_value.ToString();
                                return true;
                            }
                        }
                        if (test.Cond.Operation.Equals("<"))
                        {
                            if (test.Cond.Value < var.Value)
                            {
                                model.Variables[getVariableIndex(test.Act.Var, model)].Formula
                                    = variable_value.ToString();
                                return true;
                            }
                        }
                    }
                }
            }
            else
            {
                foreach (SimulationVariable var in req_vars)
                {
                    if (test.Cond.Operation.Equals("=="))
                    {
                        if (test.Cond.Value.Equals(var.Value))
                        {
                            return true;
                        }
                    }
                    if (test.Cond.Operation.Equals(">"))
                    {
                        if (test.Cond.Value > var.Value)
                        {
                            return true;
                        }
                    }
                    if (test.Cond.Operation.Equals("<"))
                    {
                        if (test.Cond.Value < var.Value)
                        {
                            return true;
                        }
                    }
                }
            }


            return false;
        }

        private static string getNameById(int id, Model model)
        {
            foreach (Variable var in model.Variables)
            {
                if (var.Id.Equals(id))
                {
                    return var.Name;
                }
            }
            return null;
        }

        private static int getVariableIndex(int id, Model model)
        {
            for (int i = 0; i < model.Variables.Length; i++)
            {
                if (id.Equals(model.Variables[i].Id))
                {
                    return i;
                }
            }
            return 0;
        }

        private static int getIdByName(string name, Model model)
        {
            foreach (Variable var in model.Variables)
            {
                if (var.Name.Equals(name))
                {
                    return var.Id;
                }
            }
            return -1;
        }

        private static int getVariableIndex(string name, Model model)
        {
            return getVariableIndex(getIdByName(name, model), model);
        }

        private static int getSimulationVariableIndex(List<SimulationVariable> simulation_vars, int var_id)
        {
            for (int i = 0; i < simulation_vars.Count; i++)
            {
                if (simulation_vars[i].Id.Equals(var_id))
                {
                    return i;
                }
            }
            return -1;
        }

        private static bool isStabilizing(AnalysisResult result)
        {
            return result.Status.ToString().Equals("Stabilizing");
        }

    }
}
