using System.Collections.Generic;

namespace azure_fun
{
    public class TestRequest
    {

        public class Test
        {

            public class Condition
            {
                public string Var { get; set; }
                public string Operation { get; set; }
                public int Value { get; set; }
            }

            public class Action
            {
                public string Var { get; set; }
                public string Type { get; set; }
                public int Value { get; set; }

            }

            public int Id { get; set; }

            public string Name { get; set; }

            public Condition Cond { get; set; }

            public Action Act { get; set; }

            public bool ExpectStab { get; set; }

            public string SyntaxError { get; set; }

        }

        public BioModelAnalyzer.Model Model { get; set; }

        public Test[] Tests { get; set; }

    }

}