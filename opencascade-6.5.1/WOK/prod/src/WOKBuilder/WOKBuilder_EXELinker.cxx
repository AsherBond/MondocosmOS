
#include <WOKBuilder_EXELinker.ixx>

#include <WOKUtils_Path.hxx>

#include <WOKBuilder_Executable.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#include <WOKBuilder_ManifestLibrary.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKTools_Messages.hxx>

#include <OSD_Environment.hxx>

WOKBuilder_EXELinker::WOKBuilder_EXELinker(const Handle(TCollection_HAsciiString)& aName,
					   const WOKUtils_Param&                     aParams)
: WOKBuilder_WNTLinker(aName,aParams)
{
}

Handle(TCollection_HAsciiString) WOKBuilder_EXELinker::EvalHeader()
{
  OSD_Environment env ( "WOK_LINKER" );

  Handle(TCollection_HAsciiString) retVal;

  TCollection_AsciiString val = env.Value();

 if(!env.Failed()) 
   {
     retVal = new TCollection_HAsciiString(val);

     InfoMsg() << "WOKBuilder_EXELinker::EvalHeader"
       << '\'' << retVal << "' is using" << endm;
   } 
 else
   retVal = EvalToolTemplate("LinkerHeaderEXE");

  return retVal;
}

Handle(TCollection_HAsciiString) WOKBuilder_EXELinker::EvalCFExt() 
{
  return EvalToolParameter("LinkerCFExtEXE");
}

Handle(TCollection_HAsciiString) WOKBuilder_EXELinker::EvalFooter()
{
 Handle(WOKBuilder_Entity)        outEnt[3];
 Handle(TCollection_HAsciiString) tmp;
 Handle(TCollection_HAsciiString) retVal = EvalToolParameter("LinkerOutput");
 //Standard_Boolean                 fDebug;
 
 //fDebug = Params().Value("%DebugMode")->IsSameString(new TCollection_HAsciiString("True"))  ? Standard_True : Standard_False;

 tmp = EvalToolTemplate("LinkerEXE");

 outEnt[0] = new WOKBuilder_Executable ( new WOKUtils_Path(tmp));

 retVal->AssignCat(tmp);
 retVal->AssignCat(EvalToolParameter("LinkerPDBOption"));

 //if(fDebug)
 //  {
 tmp = EvalToolTemplate ( "LinkerPDB" );
 retVal->AssignCat(tmp);
 outEnt[1] = new WOKBuilder_SharedLibrary(new WOKUtils_Path(tmp));
 //  }
#if _MSC_VER >= 1400
 outEnt[2] = new WOKBuilder_ManifestLibrary(new WOKUtils_Path(EvalToolTemplate("EXEMAN")));
#endif

 SetProduction(new WOKBuilder_HSequenceOfEntity);

 for ( int i = 0; i < 3; i++ )
 {
  if ( !outEnt[ i ].IsNull() )
    Produces()->Append( outEnt[ i ] );
 }

 return retVal;
}














