// ADN
//    
// 11/1995
//

#include <MS.hxx>
#include <MS_Method.hxx>
#include <MS_MemberMet.hxx>
#include <MS_ExternMet.hxx>
#include <MS_MetaSchema.hxx>
#include <MS_Interface.hxx>
#include <MS_Engine.hxx>
#include <MS_Class.hxx>
#include <MS_Enum.hxx>
#include <MS_StdClass.hxx>
#include <MS_Package.hxx>
#include <MS_Param.hxx>
#include <MS_MapOfMethod.hxx>
#include <MS_MapOfType.hxx>
#include <MS_MapOfGlobalEntity.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_HArray1OfParam.hxx>
#include <EDL_API.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

void CPPIntExt_ProcessAMethod(const Handle(MS_Method)& themet,
			      const Handle(MS_MetaSchema)& aMeta,
//			      const Handle(MS_Interface)& srcInterface,
			      const Handle(MS_Interface)& ,
//			      const Handle(EDL_API)& api,
			      const Handle(EDL_API)& ,
			      MS_MapOfMethod& expmap,
			      MS_MapOfType& maptype,
			      MS_MapOfType& mapusedtype,
//			      MS_MapOfGlobalEntity& mappack,
			      MS_MapOfGlobalEntity& ,
			      Standard_Boolean AddArgs)
{
  if (!themet->Private()) {
    if (MS::IsExportableMethod(aMeta,themet)) {
      expmap.Bind(themet->FullName(),themet);
      Handle(MS_Param) theret = themet->Returns();
      if (!theret.IsNull()) {
	maptype.Bind(theret->TypeName(),theret->Type());
	mapusedtype.Bind(theret->TypeName(),theret->Type());
      }
      Handle(MS_HArray1OfParam) theargs = themet->Params();
      if (!theargs.IsNull()) {
	for (Standard_Integer i=theargs->Lower(); i<= theargs->Upper(); i++) {
	  if (AddArgs) {
	    maptype.Bind(theargs->Value(i)->TypeName(),
			 theargs->Value(i)->Type());
	  }
	  mapusedtype.Bind(theargs->Value(i)->TypeName(),
			   theargs->Value(i)->Type());
	}
      }
    }
    else {
      WarningMsg() << "CPPIntExt_ProcessAMethod"
	<< "Cannot export method " << themet->FullName()->ToCString() << endm;
    }
  }
}

void CPPIntExt_ProcessAClass(const Handle(MS_Class)& theclass,
			     const Handle(MS_MetaSchema)& aMeta,
			     const Handle(MS_Interface)& srcInterface,
			     const Handle(EDL_API)& api,
			     MS_MapOfMethod& expmap,
			     MS_MapOfType& maptype,
			     MS_MapOfType& mapusedtype,
			     MS_MapOfGlobalEntity& mappack,
			     Standard_Boolean AddArgs)
{
  if (MS::IsExportableClass(aMeta,theclass,Standard_False,Standard_False)) {
    maptype.Bind(theclass->FullName(),theclass);
    mapusedtype.Bind(theclass->FullName(),theclass);
    Handle(MS_HSequenceOfMemberMet) themets = theclass->GetMethods();
    for (Standard_Integer i=1; i<= themets->Length(); i++) {
      if (!themets->Value(i)->Private()) {
	if (!themets->Value(i)->IsProtected()) {
	  CPPIntExt_ProcessAMethod(themets->Value(i),aMeta,srcInterface,api,expmap,
				   maptype,mapusedtype,mappack,AddArgs);
	}
      }
    }
  }
  else {
    WarningMsg() << "CPPIntExt_ProcessAClass" 
      << "Cannot export class " << theclass->FullName()->ToCString() << endm;
  }
}


void 
CPPIntExt_ProcessClasses(const Handle(MS_MetaSchema)& aMeta,
			 const Handle(MS_Interface)& srcInterface,
			 const Handle(EDL_API)& api,
			 MS_MapOfMethod& expmap,
			 MS_MapOfType& maptype,
			 MS_MapOfType& mapusedtype,
			 MS_MapOfGlobalEntity& mappack,
			 Standard_Boolean AddArgs)
{
  Handle(TColStd_HSequenceOfHAsciiString) classesnames = srcInterface->Classes();
  for (Standard_Integer i=1; i<= classesnames->Length(); i++) {
    Handle(MS_Class) curcl = Handle(MS_Class)::DownCast(aMeta->GetType(classesnames->Value(i)));
    CPPIntExt_ProcessAClass(curcl,aMeta,srcInterface,api,expmap,maptype,mapusedtype,mappack,AddArgs);
  }
}

void 
CPPIntExt_ProcessPackages(const Handle(MS_MetaSchema)& aMeta,
			  const Handle(MS_Interface)& srcInterface,
			  const Handle(EDL_API)& api,
			  MS_MapOfMethod& expmap,
			  MS_MapOfType& maptype,
			  MS_MapOfType& mapusedtype,
			  MS_MapOfGlobalEntity& mappack,
			  Standard_Boolean AddArgs)
{
  Handle(TColStd_HSequenceOfHAsciiString) pknames = srcInterface->Packages();
  for (Standard_Integer i=1; i<= pknames->Length(); i++) {
    Handle(MS_Package) curpk = aMeta->GetPackage(pknames->Value(i));
    Handle(MS_HSequenceOfExternMet) mets = curpk->Methods();
    Standard_Integer j;
    if (!mets->IsEmpty()) {
      mappack.Bind(curpk->Name(),curpk);
      for (j=1; j<= mets->Length(); j++) {
	CPPIntExt_ProcessAMethod(mets->Value(j),aMeta,srcInterface,api,expmap,maptype,mapusedtype,mappack,AddArgs);
      }
    }
    Handle(TColStd_HSequenceOfHAsciiString) classesnames = curpk->Classes();
    for (j=1; j<= classesnames->Length(); j++) {
      Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(pknames->Value(i),
								    classesnames->Value(j));
      Handle(MS_Class) curcl = Handle(MS_Class)::DownCast(aMeta->GetType(fullname));
      if (!curcl->Private()) {
	CPPIntExt_ProcessAClass(curcl,aMeta,srcInterface,api,expmap,maptype,mapusedtype,mappack,AddArgs);
      }
    }
    classesnames = curpk->Enums();
    for (j=1; j<= classesnames->Length(); j++) {
      Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(pknames->Value(i),
								    classesnames->Value(j));
      Handle(MS_Type) curtyp = aMeta->GetType(fullname);
      if (!curtyp->Private()) {
	maptype.Bind(curtyp->FullName(),curtyp);
	mapusedtype.Bind(curtyp->FullName(),curtyp);
      }
    }
    classesnames = curpk->Aliases();
    for (j=1; j<= classesnames->Length(); j++) {
      Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(pknames->Value(i),
								    classesnames->Value(j));
      Handle(MS_Type) curtyp = aMeta->GetType(fullname);
      if (!curtyp->Private()) {
	maptype.Bind(curtyp->FullName(),curtyp);
	mapusedtype.Bind(curtyp->FullName(),curtyp);
      }
    }
    classesnames = curpk->Pointers();
    for (j=1; j<= classesnames->Length(); j++) {
      Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(pknames->Value(i),
								    classesnames->Value(j));
      Handle(MS_Type) curtyp = aMeta->GetType(fullname);
      if (!curtyp->Private()) {
	maptype.Bind(curtyp->FullName(),curtyp);
	mapusedtype.Bind(curtyp->FullName(),curtyp);
      }
    }
    classesnames = curpk->Importeds();
    for (j=1; j<= classesnames->Length(); j++) {
      Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(pknames->Value(i),
								    classesnames->Value(j));
      Handle(MS_Type) curtyp = aMeta->GetType(fullname);
      if (!curtyp->Private()) {
	maptype.Bind(curtyp->FullName(),curtyp);
	mapusedtype.Bind(curtyp->FullName(),curtyp);
      }
    }
    classesnames = curpk->Primitives();
    for (j=1; j<= classesnames->Length(); j++) {
      Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(pknames->Value(i),
								    classesnames->Value(j));
      Handle(MS_Type) curtyp = aMeta->GetType(fullname);
      if (!curtyp->Private()) {
	maptype.Bind(curtyp->FullName(),curtyp);
	mapusedtype.Bind(curtyp->FullName(),curtyp);
      }
    }
  }
}

void 
CPPIntExt_ProcessMethods(const Handle(MS_MetaSchema)& aMeta,
			 const Handle(MS_Interface)& srcInterface,
			 const Handle(EDL_API)& api,
			 MS_MapOfMethod& expmap,
			 MS_MapOfType& maptype,
			 MS_MapOfType& mapusedtype,
			 MS_MapOfGlobalEntity& mappack,
			 Standard_Boolean AddArgs)
{
  Handle(TColStd_HSequenceOfHAsciiString) metnames = srcInterface->Methods();
  for (Standard_Integer i=1; i<= metnames->Length(); i++) {
    Handle(MS_Method) curmet = MS::GetMethodFromFriendName(aMeta,
							   metnames->Value(i));
    Handle(MS_ExternMet) extmet = Handle(MS_ExternMet)::DownCast(curmet);
    if (!extmet.IsNull()) {
      if (!extmet->Private()) {
	Handle(MS_Package) curpk = aMeta->GetPackage(extmet->Package());
	mappack.Bind(curpk->Name(),curpk);
      }
    }
    else {
      Handle(MS_MemberMet) memmet = Handle(MS_MemberMet)::DownCast(curmet);
      if (!memmet.IsNull()) {
	if (!memmet->Private()) {
	  Handle(MS_Type) curtyp = aMeta->GetType(memmet->Class());
	  maptype.Bind(curtyp->FullName(),curtyp);
	  mapusedtype.Bind(curtyp->FullName(),curtyp);
	}
      }
    }
    CPPIntExt_ProcessAMethod(curmet,aMeta,srcInterface,api,expmap,maptype,mapusedtype,mappack,AddArgs);
  }
}


void CPPIntExt_LoadMethods(const Handle(MS_MetaSchema)& aMeta,
			   const Handle(MS_Interface)& srcInterface,
			   const Handle(EDL_API)& api,
			   MS_MapOfMethod& expmap,
			   MS_MapOfType& maptype,
			   MS_MapOfType& mapusedtype,
			   MS_MapOfGlobalEntity& mappack,
			   Standard_Boolean AddArgs = Standard_False)
{
  CPPIntExt_ProcessClasses(aMeta,srcInterface,api,expmap,maptype,mapusedtype,mappack,AddArgs);
  CPPIntExt_ProcessPackages(aMeta,srcInterface,api,expmap,maptype,mapusedtype,mappack,AddArgs);
  CPPIntExt_ProcessMethods(aMeta,srcInterface,api,expmap,maptype,mapusedtype,mappack,AddArgs);
}


void CPPIntExt_LoadMethods(const Handle(MS_MetaSchema)& aMeta,
//			   const Handle(MS_Engine)& srcEngine,
			   const Handle(MS_Engine)& ,
			   const Handle(EDL_API)& api,
			   MS_MapOfMethod& expmap,
			   MS_MapOfType& maptype,
			   MS_MapOfGlobalEntity& mappack,
			   const Handle(TColStd_HSequenceOfHAsciiString)& seqint)
{
  MS_MapOfType alltypes;
  for (Standard_Integer i=1; i<= seqint->Length(); i++) {
    Handle(MS_Interface) srcint = aMeta->GetInterface(seqint->Value(i));
    CPPIntExt_LoadMethods(aMeta,srcint,api,expmap,maptype,alltypes,mappack,Standard_True);
  }
}
