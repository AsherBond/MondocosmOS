#include <WOKBuilder_WNTLinker.ixx>

#include <WOKUtils_Path.hxx>
#include <WOKBuilder_Library.hxx>

WOKBuilder_WNTLinker::WOKBuilder_WNTLinker(const Handle(TCollection_HAsciiString)& aName,
					   const WOKUtils_Param&                   aParams)
: WOKBuilder_WNTCollector( aName, aParams ) 
{
}

void WOKBuilder_WNTLinker::ProduceDEFile(const Handle(WOKBuilder_DEFile)& aDEFile) 
{
  Handle(TCollection_HAsciiString) defLine = EvalToolParameter("LinkerDEFSwitch");

  if(!defLine.IsNull()) 
    {
      defLine->AssignCat(aDEFile->Path()->Name());
      defLine->AssignCat("\r\n");

      myCommandFile.Write(defLine->String(), defLine->Length());
    }
}
  
void WOKBuilder_WNTLinker::ProduceLibraryList(const Handle(WOKBuilder_HSequenceOfLibrary)& aLibList) 
{
  for ( int i=1; i<=aLibList->Length(); ++i) 
    {
      TCollection_AsciiString line =  aLibList->Value(i)->Path()->Name()->String();

      line.AssignCat("\r\n");
      myCommandFile.Write(line,line.Length());
    } 
} 

void WOKBuilder_WNTLinker::ProduceExternList(const Handle(TColStd_HSequenceOfHAsciiString)& anExternList)
{
  for(Standard_Integer i=1; i<=anExternList->Length(); ++i ) 
    {
      TCollection_AsciiString line = anExternList->Value(i)->String();
 
      line.AssignCat("\r\n");
      myCommandFile.Write(line, line.Length());
    }
}

