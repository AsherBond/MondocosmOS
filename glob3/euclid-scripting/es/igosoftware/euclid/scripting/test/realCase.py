from es.igosoftware.euclid.experimental.algorithms import IAlgorithm;

class Normal(IAlgorithm):

	def getName(self):
		return "name"

	def getDescription(self):
		return "description"

	def apply(self, parameters):
		return 3.14159

ALGORITHM = Normal()
