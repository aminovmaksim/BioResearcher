using System;
using System.Collections.Generic;
using System.Text;
using static azure_fun.TestRequest;

namespace azure_fun
{
	public class SingleTestRequest
	{
		public BioModelAnalyzer.Model Model { get; set; }

		public Test test { get; set; }
	}
}
