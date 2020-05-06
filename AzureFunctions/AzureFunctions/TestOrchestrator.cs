using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using azure_fun;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.DurableTask;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace Company.Function
{
    public static class TestOrchestrator
    {
        [FunctionName("HttpStartTests")]
        public static async Task<HttpResponseMessage> HttpStart(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post")] HttpRequestMessage req,
            [DurableClient] IDurableOrchestrationClient starter,
            ILogger log)
        {

            string request = await req.Content.ReadAsStringAsync();

            // Function input comes from the request content.
            string instanceId = await starter.StartNewAsync("RunBioModelTests", null, request);

            log.LogInformation($"Started orchestration with ID = '{instanceId}'.");

            return starter.CreateCheckStatusResponse(req, instanceId);
        }

        [FunctionName("RunBioModelTests")]
        public static async Task<string> RunTests(
            [OrchestrationTrigger] IDurableOrchestrationContext testsContext, ILogger log)
        {
            string json = testsContext.GetInput<string>()?.Trim();
            TestRequest req = JsonConvert.DeserializeObject<TestRequest>(json);

            var tasks = new Task<TestResponse>[req.Tests.Length];

            for (int i = 0; i < tasks.Length; i++)
            {
                SingleTestRequest singleTestRequest = new SingleTestRequest
                {
                    Model = req.Model,
                    test = req.Tests[i]
                };
                tasks[i] = testsContext.CallActivityAsync<TestResponse>(
                    "RunSingleTest",
                    singleTestRequest);
            }

            await Task.WhenAll(tasks);

            TestResponse[] results = new TestResponse[tasks.Length];
            
            for (int i = 0; i < tasks.Length; i++)
            {
                results[i] = tasks[i].Result;
            }

            string result = JsonConvert.SerializeObject(results);
            log.LogInformation(result);

            return result;
        }

        [FunctionName("RunSingleTest")]
        public static TestResponse RunSingleTest(
            [ActivityTrigger] SingleTestRequest test,
            ILogger log)
        {
            if (test.test.SyntaxError != null)
            {
                TestResponse testResponse = new TestResponse();
                testResponse.Id = test.test.Id;
                testResponse.Name = test.test.Name;
                testResponse.SyntaxError = test.test.SyntaxError;
                testResponse.TestSuccess = false;
                return testResponse;
            }
            return TestingUtil.Run(test);
        }

    }

}