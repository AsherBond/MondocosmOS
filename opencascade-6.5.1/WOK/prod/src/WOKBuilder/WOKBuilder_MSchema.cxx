

#include <Standard_ProgramError.hxx>


#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_Package.hxx>
#include <MS_Schema.hxx>
#include <MS_StdClass.hxx>
#include <MS_Error.hxx>
#include <MS_InstClass.hxx>
#include <MS_GenClass.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_ExternMet.hxx>
#include <MS_MemberMet.hxx>
#include <MS_InstMet.hxx>
#include <MS_HSequenceOfParam.hxx>
#include <MS_Param.hxx>
#include <MS_NatType.hxx>
#include <MS.hxx>
#include <MS_Component.hxx>
#include <MS_Executable.hxx>
#include <MS_ExecPart.hxx>
#include <MS_HSequenceOfExecPart.hxx>
#include <MS_ExecFile.hxx>
#include <MS_HSequenceOfExecFile.hxx>
#include <MS_DataMapIteratorOfMapOfType.hxx>

#include <WOKBuilder_CDLFile.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_SequenceOfEntity.hxx>

#include <WOKBuilder_MSchema.ixx>

Standard_IMPORT void MS_ClearMapOfName();
Standard_IMPORT const Handle(TCollection_HAsciiString)& MS_GetName(const Handle(TCollection_HAsciiString)&);

//=======================================================================
//function : WOKBuilder_MSchema
//purpose  : 
//=======================================================================
WOKBuilder_MSchema::WOKBuilder_MSchema()
{
  myschema = new MS_MetaSchema;
}


//                               Type And Global Entities Management

//=======================================================================
//function : IsDefined
//purpose  : 
//=======================================================================
Standard_Boolean WOKBuilder_MSchema::IsDefined(const Handle(TCollection_HAsciiString)& anentity) const
{
  if(myschema->IsDefined(anentity)) 
    {
      Handle(MS_Class) aclass = Handle(MS_Class)::DownCast(myschema->GetType(anentity));

      if(aclass.IsNull() == Standard_True) return Standard_True;

      return !aclass->Incomplete();
    }
  if(myschema->IsPackage(anentity))    return Standard_True;
  if(myschema->IsInterface(anentity))  return Standard_True;
  if(myschema->IsClient(anentity))     return Standard_True;
  if(myschema->IsSchema(anentity))     return Standard_True;
  if(myschema->IsEngine(anentity))     return Standard_True;
  if(myschema->IsExecutable(anentity)) return Standard_True;
  if(myschema->IsComponent(anentity))  return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : RemoveEntity
//purpose  : 
//=======================================================================
void WOKBuilder_MSchema::RemoveEntity(const Handle(TCollection_HAsciiString)& anentity)
{
  Handle(TCollection_HAsciiString) astr;
  if(myschema->IsPackage(anentity))         myschema->RemovePackage(anentity);
  else if(myschema->IsInterface(anentity))  myschema->RemoveInterface(anentity);
  else if(myschema->IsClient(anentity))     myschema->RemoveClient(anentity);
  else if(myschema->IsEngine(anentity))     myschema->RemoveEngine(anentity);
  else if(myschema->IsExecutable(anentity)) myschema->RemoveExecutable(anentity);
  else if(myschema->IsSchema(anentity))     myschema->RemoveSchema(anentity);
  else if(myschema->IsComponent(anentity))  myschema->RemoveComponent(anentity);
  return;
}

//=======================================================================
//function : RemoveType
//purpose  : 
//=======================================================================
void WOKBuilder_MSchema::RemoveType(const Handle(TCollection_HAsciiString)& anentity)
{
  myschema->RemoveType(anentity,Standard_False);
}

//=======================================================================
//function : GetEntityTypes
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::GetEntityTypes(const Handle(TCollection_HAsciiString)& anentity) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Handle(TCollection_HAsciiString) fullname;
  Standard_Integer j;

  if(IsDefined(anentity))
    {
      
      if(myschema->IsPackage(anentity))
	{
	  const Handle(MS_Package)& apk = myschema->GetPackage(anentity);
	  
	  if(!apk.IsNull())
	    {
	      aseq->Append(anentity);
	      
	      for(j=1; j<=apk->Classes()->Length(); j++)
		{
		  fullname = MS::BuildFullName(anentity, apk->Classes()->Value(j));
		  aseq->Append(fullname);
		}
	      for(j=1; j<=apk->Excepts()->Length(); j++)
		{
		  fullname = MS::BuildFullName(anentity, apk->Excepts()->Value(j));
		  aseq->Append(fullname);
		}
	      for(j=1; j<=apk->Enums()->Length(); j++)
		{
		  fullname = MS::BuildFullName(anentity, apk->Enums()->Value(j));
		  aseq->Append(fullname);
		}
	      for(j=1; j<=apk->Aliases()->Length(); j++)
		{
		  fullname = MS::BuildFullName(anentity, apk->Aliases()->Value(j));
		  aseq->Append(fullname);
		}
	      for(j=1; j<=apk->Pointers()->Length(); j++)
		{
		  fullname = MS::BuildFullName(anentity, apk->Pointers()->Value(j));
		  aseq->Append(fullname);
		}
	      for(j=1; j<=apk->Importeds()->Length(); j++)
		{
		  fullname = MS::BuildFullName(anentity, apk->Importeds()->Value(j));
		  aseq->Append(fullname);
		}
	      for(j=1; j<=apk->Primitives()->Length(); j++)
		{
		  fullname = MS::BuildFullName(anentity, apk->Primitives()->Value(j));
		  aseq->Append(fullname);
		}
	      return aseq;
	    }
	}
      else
	{
	  if(myschema->IsInterface(anentity) || 
	     myschema->IsClient(anentity)    ||
	     myschema->IsEngine(anentity)    ||
	     myschema->IsSchema(anentity)    ||
	     myschema->IsExecutable(anentity)||
	     myschema->IsComponent(anentity))
	    {
	      aseq->Append(anentity);
	    }
	}
    }
  return aseq;
}

//=======================================================================
//function : RemoveAutoTypes
//purpose  : 
//=======================================================================
void WOKBuilder_MSchema::RemoveAutoTypes() const
{
  MS_DataMapIteratorOfMapOfType anit = myschema->Types();
  Handle(TColStd_HSequenceOfHAsciiString) toshoot = new TColStd_HSequenceOfHAsciiString;
  Handle(MS_Class)     aclass;
  Handle(MS_InstClass) instclass;
  Handle(MS_StdClass)  stdclass;
  Handle(MS_Error)     except;
  Standard_Integer i;
  

  while(anit.More())
    {
      aclass = Handle(MS_Class)::DownCast(anit.Value());
	  
      if(!aclass.IsNull())
	{
	  if(!aclass->IsNested())
	    {
	      if(!aclass->IsKind(STANDARD_TYPE(MS_Error)))
		{
		  stdclass = Handle(MS_StdClass)::DownCast(aclass);
		  if(!stdclass.IsNull())
		    {
		      if(!stdclass->IsGeneric())
			{
			  instclass = stdclass->GetMyCreator();
			  if(!instclass.IsNull())
			    {
			      toshoot->Append(stdclass->FullName());
			    }
			}
		    }
		}
	    }
	}
      anit.Next();
    }
  for(i=1; i<=toshoot->Length(); i++)
    {
      stdclass = Handle(MS_StdClass)::DownCast(myschema->GetType(toshoot->Value(i)));
      if(!stdclass.IsNull())
	{
	  instclass = stdclass->GetMyCreator();
	  if(!instclass.IsNull())
	    {
	      WOK_TRACE {
		VerboseMsg()("WOK_MSCHEMA") << "WOKBuilder_MSchema::RemoveAutoTypes" 
					  << "Shooting Auto type : " << toshoot->Value(i) << endm;
	      }
	      myschema->RemoveType(toshoot->Value(i), Standard_False);
	      instclass->Initialize();
	      myschema->AddType(instclass);
	    }
	}
    }
}


//                      WOK and MetaSchema Conventions



//=======================================================================
//function : AssociatedFile
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_MSchema::AssociatedFile(const Handle(TCollection_HAsciiString)& anentity) const
{
  Handle(TCollection_HAsciiString) filename;
  Handle(MS_InstClass) instclass;
  Handle(MS_GenClass)  genclass;
  Handle(MS_StdClass) stdclass;
  Handle(MS_Class)    aclass;

  if(myschema->IsPackage(anentity))
    {
      filename = new TCollection_HAsciiString(anentity);
      filename->AssignCat(".cdl");
      return filename;
    }
  if(myschema->IsDefined(anentity))
    {
      const Handle(MS_Type)& atype = myschema->GetType(anentity);

      if(atype->IsKind(STANDARD_TYPE(MS_NatType)))
	{
	  // Nat Type : PK.cdl
	  filename = AssociatedEntity(anentity);
	  filename->AssignCat(".cdl");
	  return filename;
	}
      if(atype->IsKind(STANDARD_TYPE(MS_GenClass)))
	{
	  // GenClass : PK_GenClass.cdl
	  filename = new TCollection_HAsciiString(anentity);
	  filename->AssignCat(".cdl");
	  return filename;
	}
      if(atype->IsKind(STANDARD_TYPE(MS_Error)))
	{
	  // Execption : PK.cdl
	  filename = AssociatedEntity(anentity);
	  filename->AssignCat(".cdl");
	  return filename;
	}
      
      
      aclass = Handle(MS_Class)::DownCast(atype);

      if(!aclass.IsNull())
	{
	  if(aclass->IsNested())
	    {
	      // Nested Class : PK_NestingGenClass.cdl
	      return AssociatedFile(aclass->GetNestingClass());
	    }
	  
	  
	  stdclass = Handle(MS_StdClass)::DownCast(atype);
	  if(!stdclass.IsNull())
	    {
	      if(stdclass->GetMyCreator().IsNull())
		{
		  // StdClasss
		  filename = new TCollection_HAsciiString(anentity);
		  filename->AssignCat(".cdl");
		  return filename;
		}
	      else
		{
		  // Instantiation : PK.cdl
		  filename = AssociatedEntity(anentity);
		  filename->AssignCat(".cdl");
		  return filename;
		}
	    }

	  instclass = Handle(MS_InstClass)::DownCast(atype);

	  if(!instclass.IsNull())
	    {
	      // Root Instantiation : PK.cdl
	      filename = AssociatedEntity(anentity);
	      filename->AssignCat(".cdl");
	      return filename;
	    }
	}
    }
  filename = new TCollection_HAsciiString(anentity);
  filename->AssignCat(".cdl");
  return filename;
}


//=======================================================================
//function : AssociatedEntity
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_MSchema::AssociatedEntity(const Handle(TCollection_HAsciiString)& atype) const
{
  Handle(TCollection_HAsciiString) entityname;
  Standard_Integer apos;

  if((apos = atype->Location(1, '_', 1, atype->Length())) == 0)
    {
      entityname = new TCollection_HAsciiString(atype);
    }
  else
    {
      entityname = atype->SubString(1, apos-1);
    }
  return entityname;
}

//=======================================================================
//function : TypeSourceFiles
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::TypeSourceFiles(const Handle(TCollection_HAsciiString)& type) const
{
  Handle(TCollection_HAsciiString)        astr;
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;

  if(myschema->IsDefined(type))
    {
      const Handle(MS_Type)& mstype = myschema->GetType(type);

      Handle(MS_Class) aclass = Handle(MS_Class)::DownCast(mstype);
      if(!aclass.IsNull())
	{
	  if(aclass->IsNested())
	    {
	      if(aclass->IsKind(STANDARD_TYPE(MS_StdClass)))
		{
		  if(myschema->GetType(aclass->GetNestingClass())->IsKind(STANDARD_TYPE(MS_GenClass)))
		    {
		      astr = new TCollection_HAsciiString(type);
		      astr->AssignCat(".gxx");
		      aseq->Append(astr);
		    }
		  else return aseq;
		}
	      else return aseq;
	    }
	  else
	    {
	      Handle(MS_StdClass) msstdclass = Handle(MS_StdClass)::DownCast(mstype);
	      if(!msstdclass.IsNull())
		{
		  if(!msstdclass->GetMyCreator().IsNull())
		    {
		      return aseq;
		    }
		  if(!msstdclass->IsKind(STANDARD_TYPE(MS_Error)))
		    {
		      astr = new TCollection_HAsciiString(type);
		      astr->AssignCat(".cxx");
		      aseq->Append(astr);
		    }
		}
	      
	      Handle(MS_Class) msclass = Handle(MS_Class)::DownCast(mstype);
	      if(!msclass.IsNull())
		{
		  if(msclass->IsKind(STANDARD_TYPE(MS_GenClass)))
		    {
		      astr = new TCollection_HAsciiString(type);
		      astr->AssignCat(".gxx");
		      aseq->Append(astr);
		    }
		}
	    }

	  Handle(MS_HSequenceOfMemberMet) methseq = Handle(MS_Class)::DownCast(mstype)->GetMethods();
	  
	  for(Standard_Integer i=1; i<=methseq->Length(); i++)
	    {
	      if(methseq->Value(i)->IsInline())
		{
		  astr = new TCollection_HAsciiString(type);
		  astr->AssignCat(".lxx");
		  aseq->Append(astr);
		  break;
		}
	    }
	}
    }
  else if(myschema->IsPackage(type))
    {
      Handle(MS_Package) apk = myschema->GetPackage(type);

      if(apk->Methods()->Length() != 0)
	{
	  Handle(MS_Method) ameth;

	  astr = new TCollection_HAsciiString(type);
	  astr->AssignCat(".cxx");
	  aseq->Append(astr);
	  
	  for(Standard_Integer i=1 ; i<=apk->Methods()->Length(); i++)
	    {

	      if(apk->Methods()->Value(i)->IsInline())
		{
		  astr = new TCollection_HAsciiString(type);
		  astr->AssignCat(".lxx");
		  aseq->Append(astr);
		  break;
		}
	    }
	}
    }
  return aseq;
}


//=======================================================================
//function : ExecFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_MSchema::ExecFileName(const Handle(MS_ExecFile)& afile) const 
{
  Handle(TCollection_HAsciiString) astr;

  astr = new TCollection_HAsciiString(afile->Name());
  
  switch(afile->Language())
    {
    case MS_FORTRAN:
      astr->AssignCat(".f");
      break;
    case MS_CPP:
      astr->AssignCat(".cxx");
      break;
    case MS_C:
      astr->AssignCat(".c");
      break;
    case MS_OBJECT:
      astr->AssignCat(".o");
      break;
    }
  return astr;
}

//=======================================================================
//function : ExecutableParts
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableParts(const Handle(TCollection_HAsciiString)& anexec) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  
  Standard_Integer i;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      astr = parts->Value(i)->Name();
      if(!amap.Contains(astr))
	{
	  amap.Add(astr);
	  aseq->Append(astr);
	}
    }

  return aseq;
}

//=======================================================================
//function : ExecutableFiles
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableFiles(const Handle(TCollection_HAsciiString)& anexec) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  
  Standard_Integer i,j;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  Handle(MS_HSequenceOfExecFile) files;
  Handle(TCollection_HAsciiString) astr;
  WOKTools_MapOfHAsciiString amap;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      files = parts->Value(i)->Files();

      for(j=1; j<=files->Length(); j++)
	{
	  astr = ExecFileName(files->Value(j));

	  if(!amap.Contains(astr))
	    {
	      amap.Add(astr);
	      aseq->Append(astr);
	    }
	}
    }

  return aseq;
}

//=======================================================================
//function : ExecutableFiles
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableFiles(const Handle(TCollection_HAsciiString)& anexec,
									    const Handle(TCollection_HAsciiString)& apart) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i,j;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  Handle(MS_HSequenceOfExecFile) files;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      if(parts->Value(i)->Name()->IsSameString(apart))
	{
	  files = parts->Value(i)->Files();
	  for(j=1; j<=files->Length(); j++)
	    {
	      astr = ExecFileName(files->Value(j));
	      if(!amap.Contains(astr))
		{
		  amap.Add(astr);
		  aseq->Append(astr);
		}
	    }
	  return aseq;
	}
    }

  return aseq;
}

//=======================================================================
//function : ExecutableModules
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableModules(const Handle(TCollection_HAsciiString)& anexec) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i,j;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  Handle(MS_HSequenceOfExecFile) files;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      files = parts->Value(i)->Files();

      for(j=1; j<=files->Length(); j++)
	{
	  astr = files->Value(i)->Name();
	  if(!amap.Contains(astr))
	    {
	      amap.Add(astr);
	      aseq->Append(astr);
	    }
	}
    }

  return aseq;
}

//=======================================================================
//function : ExecutableModules
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableModules(const Handle(TCollection_HAsciiString)& anexec,
									      const Handle(TCollection_HAsciiString)& apart) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i,j;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  Handle(MS_HSequenceOfExecFile) files;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      if(parts->Value(i)->Name()->IsSameString(apart))
	{
	  files = parts->Value(i)->Files();
	  for(j=1; j<=files->Length(); j++)
	    {
	      astr = files->Value(j)->Name();
	      if(!amap.Contains(astr))
		{
		  amap.Add(astr);
		  aseq->Append(astr);
		}
	    }
	  return aseq;
	}
    }
  return aseq;
}

//=======================================================================
//function : ExecutableLibraries
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableLibraries(const Handle(TCollection_HAsciiString)& anexec) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i,j;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  Handle(TColStd_HSequenceOfHAsciiString) libs;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      libs = parts->Value(i)->Libraries();

      for(j=1; j<=libs->Length(); j++)
	{
	  astr = libs->Value(i);
	  if(!amap.Contains(astr))
	    {
	      amap.Add(astr);
	      aseq->Append(astr);
	    }
	}
    }

  return aseq;
}

//=======================================================================
//function : ExecutableLibraries
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableLibraries(const Handle(TCollection_HAsciiString)& anexec,
										const Handle(TCollection_HAsciiString)& apart) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i,j;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  Handle(TColStd_HSequenceOfHAsciiString) libs;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      if(parts->Value(i)->Name()->IsSameString(apart))
	{
	  libs = parts->Value(i)->Libraries();
	  for(j=1; j<=libs->Length(); j++)
	    {
	      astr = libs->Value(j);
	      if(!amap.Contains(astr))
		{
		  amap.Add(astr);
		  aseq->Append(astr);
		}
	    }
	  return aseq;
	}
    }
  return aseq;
}

//=======================================================================
//function : ExecutableExternals
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableExternals(const Handle(TCollection_HAsciiString)& anexec) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i,j;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  Handle(TColStd_HSequenceOfHAsciiString) externals;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      externals = parts->Value(i)->Externals();

      for(j=1; j<=externals->Length(); j++)
	{
	  astr = externals->Value(i);
	  if(!amap.Contains(astr))
	    {
	      amap.Add(astr);
	      aseq->Append(astr);
	    }
	}
    }

  return aseq;
}

//=======================================================================
//function : ExecutableExternals
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ExecutableExternals(const Handle(TCollection_HAsciiString)& anexec,
										const Handle(TCollection_HAsciiString)& apart) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i,j;
  Handle(MS_Executable) anexe;
  Handle(MS_HSequenceOfExecPart) parts;
  Handle(TColStd_HSequenceOfHAsciiString) externals;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  anexe = MetaSchema()->GetExecutable(anexec);
  
  parts = anexe->Parts();

  for(i=1; i<=parts->Length(); i++)
    {
      if(parts->Value(i)->Name()->IsSameString(apart))
	{
	  externals = parts->Value(i)->Externals();
	  for(j=1; j<=externals->Length(); j++)
	    {
	      astr = externals->Value(j);
	      if(!amap.Contains(astr))
		{
		  amap.Add(astr);
		  aseq->Append(astr);
		}
	    }
	  return aseq;
	}
    }
  return aseq;
}

//=======================================================================
//function : ComponentParts
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::ComponentParts(const Handle(TCollection_HAsciiString)& anexec) const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  
  Standard_Integer i;
  Handle(MS_Component) acomp;
  Handle(TColStd_HSequenceOfHAsciiString) ints;
  WOKTools_MapOfHAsciiString amap;
  Handle(TCollection_HAsciiString) astr;

  acomp = MetaSchema()->GetComponent(anexec);
  
  ints = acomp->Interfaces();

  for(i=1; i<=ints->Length(); i++)
    {
      astr = ints->Value(i);
      if(!amap.Contains(astr))
	{
	  amap.Add(astr);
	  aseq->Append(astr);
	}
    }

  return aseq;
}

//=======================================================================
//function : SchemaClasses
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::SchemaClasses(const Handle(TCollection_HAsciiString)& aschema) const
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i;
  
  Handle(TColStd_HSequenceOfHAsciiString) aseq = MetaSchema()->GetPersistentClassesFromSchema(aschema);

  for(i=1; i<=aseq->Length(); i++)
    {
      result->Append(aseq->Value(i));
    }
  
  Handle(TColStd_HSequenceOfHAsciiString) aseq2 = MetaSchema()->GetPersistentClassesFromClasses(aseq, Standard_False);
  
  for(i=1; i<=aseq2->Length(); i++)
    {
      result->Append(aseq2->Value(i));
    }

  return result;
}

//=======================================================================
//function : SortedSchemaClasses
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::SortedSchemaClasses(const Handle(TCollection_HAsciiString)& aschema) const
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;
  Handle(MS_Class) aclass;
  Standard_Integer i,j;
  
  Handle(TColStd_HSequenceOfHAsciiString) aseq = SchemaClasses(aschema);
  Handle(TColStd_HSequenceOfHAsciiString) inherits;
  WOKTools_MapOfHAsciiString amap;
  
  for(i=1; i<=aseq->Length(); i++)
    {
      aclass = Handle(MS_Class)::DownCast(MetaSchema()->GetType(aseq->Value(i)));

      if(aclass.IsNull())
	{
	  ErrorMsg() << "WOKBuilder_MSchema::SortedSchemaClasses"
		   << "Name " << aseq->Value(i) << " is not a known class name" << endm;
	  Handle(TColStd_HSequenceOfHAsciiString) result;
	  return result;
	}

      inherits = aclass->GetFullInheritsNames();

      for(j=1; j<=inherits->Length(); j++)
	{
	  if(!MS::GetStorableRootName()->IsSameString(inherits->Value(j)))
	    {
	      if(!amap.Contains(inherits->Value(j)))
		{
		  result->Append(inherits->Value(j));
		  amap.Add(inherits->Value(j));
		}
	    }
	  else
	    break;
	}

      if(!MS::GetStorableRootName()->IsSameString(aseq->Value(i)))
	{
	  if(!amap.Contains(aseq->Value(i)))
	    {
	      result->Append(aseq->Value(i));
	      amap.Add(aseq->Value(i));
	    }
	}
    }

  return result;
}

//=======================================================================
//function : SchemaDescrMissingClasses
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSchema::SchemaDescrMissingClasses(const Handle(TCollection_HAsciiString)& aschema) const
{
  Handle(TColStd_HSequenceOfHAsciiString) result;
  
  Handle(TColStd_HSequenceOfHAsciiString) aseq = MetaSchema()->GetPersistentClassesFromSchema(aschema);
  result = MetaSchema()->GetPersistentClassesFromClasses(aseq, Standard_False);
  
  return result;
}


//=======================================================================
//function : IsActionDefined
//purpose  : 
//=======================================================================
Standard_Boolean WOKBuilder_MSchema::IsActionDefined(const WOKBuilder_MSActionID& anid) const
{  
  WOKBuilder_MSActionID theid = GetStoredActionID(anid);

  return myactions.IsBound(theid);
}

//=======================================================================
//function : GetStoredActionID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionID WOKBuilder_MSchema::GetStoredActionID(const WOKBuilder_MSActionID& anid) const
{
  WOKBuilder_MSActionID theid = anid;

  switch(theid.Type())
    {
    case WOKBuilder_TypeModified:
    case WOKBuilder_Package:
    case WOKBuilder_Interface:
    case WOKBuilder_Client:
    case WOKBuilder_Engine:
    case WOKBuilder_Executable:
    case WOKBuilder_Component:
    case WOKBuilder_Schema:
    case WOKBuilder_SchUses:
    case WOKBuilder_DirectUses:
    case WOKBuilder_Uses:
    case WOKBuilder_GlobEnt:
    case WOKBuilder_Instantiate:
    case WOKBuilder_InstToStd:
    case WOKBuilder_InterfaceTypes:
    case WOKBuilder_SchemaTypes:
    case WOKBuilder_PackageMethods:
    case WOKBuilder_GenType:
    case WOKBuilder_CompleteType:
    case WOKBuilder_SchemaType:
    case WOKBuilder_Inherits:
    case WOKBuilder_TypeUses:
      theid.SetType(WOKBuilder_TypeModified);
      break;
    case WOKBuilder_TypeExtracted:
    case WOKBuilder_ClientExtract:
    case WOKBuilder_HeaderExtract:
    case WOKBuilder_SchemaExtract:
    case WOKBuilder_ServerExtract:
    case WOKBuilder_EngineExtract:
    case WOKBuilder_TemplateExtract:
      break;
    default:
      Standard_ProgramError::Raise("WOKBuilder_MSchema::GetStoredActionID : Unknown action type");
      break;
    }
  return theid;
}

//=======================================================================
//function : GetAction
//purpose  : 
//=======================================================================
Handle(WOKBuilder_MSAction) WOKBuilder_MSchema::GetAction(const WOKBuilder_MSActionID& anid) 
{
  if(myactions.IsBound(anid)) return myactions.Find(anid);
  else
    {
      Handle(WOKBuilder_MSEntity) anent;
      Handle(WOKBuilder_MSAction) result, theresult;
      WOKBuilder_MSActionID theid = GetStoredActionID(anid);
      
      
      if(!myactions.IsBound(theid))
	{
	  if(myentities.IsBound(theid.Name()))
	    {
	      anent = myentities.Find(theid.Name());
	    }
	  else
	    {
	      const Handle(TCollection_HAsciiString)& thename = MS_GetName(anid.Name());
	      anent = new WOKBuilder_MSEntity(thename);
	      myentities.Bind(thename, anent);
	    }
	  
	  WOK_TRACE {
	    VerboseMsg()("WOK_MSCHEMA") << "WOKBuilder_MSchema::GetAction"
				      << "Created Action " << theid.Name() << endm;
	  }
	  
	  result = new WOKBuilder_MSAction(anent, anid.Type());
	}
      else
	{
	  theresult = myactions.Find(theid);
	  
	  WOK_TRACE {
	    VerboseMsg()("WOK_MSCHEMA") << "WOKBuilder_MSchema::GetAction"
				      << "Found Action " << theid.Name() << " with date : " << (Standard_Integer) theresult->Date()<< endm;
	  }
	  
	  if(theresult->Type() == anid.Type()) 
	    result = theresult;
	  else
	    result = new WOKBuilder_MSAction(theresult, anid.Type());
	}
      return result;
    }
}

//=======================================================================
//function : 
//purpose  : 
//=======================================================================
void WOKBuilder_MSchema::ChangeActionToFailed(const WOKBuilder_MSActionID& anid)
{
  WOKBuilder_MSActionID theid = GetStoredActionID(anid);
  Handle(WOKBuilder_MSAction) theaction;

  if(!myactions.IsBound(theid))
    {
      theaction = GetAction(anid);
      myactions.Bind(theid, theaction);
    }
  else
    {
      theaction = myactions.Find(theid);
    }

  theaction->SetDate(-1);
  theaction->SetStatus(WOKBuilder_HasFailed);

  WOK_TRACE {
    VerboseMsg()("WOK_MSCHEMA") << "WOKBuilder_MSchema::"
			      << "Failed Action " << theid.Name() << endm;
  }
  return;
}

//=======================================================================
//function : AddAction
//purpose  : 
//=======================================================================
void WOKBuilder_MSchema::ChangeAddAction(const WOKBuilder_MSActionID& anid, const Handle(WOKBuilder_Specification)& afile)
{
  WOKBuilder_MSActionID theid = GetStoredActionID(anid);
  Handle(WOKBuilder_MSAction) theaction;

  if(!myactions.IsBound(theid))
    {
      theaction = GetAction(theid);
      myactions.Bind(theid, theaction);
    }
  else
    {
      theaction = myactions.Find(theid);
    }

  theaction->Entity()->SetFile(afile);
  theaction->GetDate();
  theaction->SetStatus(WOKBuilder_UpToDate);

  WOK_TRACE {
    VerboseMsg()("WOK_MSCHEMA") << "WOKBuilder_MSchema::ChangeAddAction"
			      << "Added Action " << theid.Name() << " with date " << (Standard_Integer) theaction->Date() << endm;
  }
  return;
}

//=======================================================================
//function : GetActionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSchema::GetActionStatus(const WOKBuilder_MSActionID& anactionid)
{
  WOKBuilder_MSActionID theid =  GetStoredActionID(anactionid);
  Handle(WOKBuilder_MSAction) theaction;

  if(!myactions.IsBound(theid)) 
    {
      WOK_TRACE {
	VerboseMsg()("WOK_MSCHEMA") << "WOKBuilder_MSchema::GetActionStatus"
				  << "Could not get Action " << theid.Name() << endm;
      }
      
      return WOKBuilder_NotDefined;
    }
  WOK_TRACE {
    VerboseMsg()("WOK_MSCHEMA") << "WOKBuilder_MSchema::GetActionStatus"
			      << "Got Action " << theid.Name() << endm;
  }
  
  return GetAction(theid)->Status();
}

//=======================================================================
//function : RemoveAction
//purpose  : 
//=======================================================================
void WOKBuilder_MSchema::RemoveAction(const WOKBuilder_MSActionID& anid)
{
  WOKBuilder_MSActionID theid  = GetStoredActionID(anid);
  Handle(WOKBuilder_MSAction) theaction;
  Handle(WOKBuilder_MSEntity) theent;

  switch(anid.Type())
    {
    case WOKBuilder_Package:
    case WOKBuilder_Interface:
    case WOKBuilder_Client:
    case WOKBuilder_Engine:
    case WOKBuilder_Executable:
    case WOKBuilder_Component:
    case WOKBuilder_Schema:
    case WOKBuilder_SchUses:
    case WOKBuilder_DirectUses:
    case WOKBuilder_Uses:
    case WOKBuilder_GlobEnt:
      RemoveEntity(anid.Name());
      break;
    case WOKBuilder_InterfaceTypes:
    case WOKBuilder_SchemaTypes:
    case WOKBuilder_PackageMethods:
      break;
    case WOKBuilder_TypeModified:
    case WOKBuilder_Instantiate:
    case WOKBuilder_InstToStd:
    case WOKBuilder_GenType:
    case WOKBuilder_SchemaType:
    case WOKBuilder_CompleteType:
    case WOKBuilder_Inherits:
    case WOKBuilder_TypeUses:
      RemoveType(anid.Name());
      break;
    case WOKBuilder_TypeExtracted:
    case WOKBuilder_ClientExtract:
    case WOKBuilder_HeaderExtract:
    case WOKBuilder_SchemaExtract:
    case WOKBuilder_ServerExtract:
    case WOKBuilder_EngineExtract:
    case WOKBuilder_TemplateExtract:
      //theid.SetType(WOKBuilder_TypeExtracted);
      break;
    default:
      Standard_ProgramError::Raise("WOKBuilder_MSchema::RemoveAction : Unknown action type");
      break;
    }

  if(myactions.IsBound(theid))
    {
      myactions.UnBind(theid);
    }
  
  WOKBuilder_MSActionID thependantid = theid;

  switch(theid.Type())
    {
    case WOKBuilder_TypeModified:
      thependantid.SetType(WOKBuilder_TypeExtracted);
      if(myactions.IsBound(thependantid)) myactions.UnBind(thependantid);
      thependantid.SetType(WOKBuilder_ClientExtract);
      if(myactions.IsBound(thependantid)) myactions.UnBind(thependantid);
      thependantid.SetType(WOKBuilder_HeaderExtract);
      if(myactions.IsBound(thependantid)) myactions.UnBind(thependantid);
      thependantid.SetType(WOKBuilder_SchemaExtract);
      if(myactions.IsBound(thependantid)) myactions.UnBind(thependantid);
      thependantid.SetType(WOKBuilder_ServerExtract);
      if(myactions.IsBound(thependantid)) myactions.UnBind(thependantid);
      thependantid.SetType(WOKBuilder_EngineExtract);
      if(myactions.IsBound(thependantid)) myactions.UnBind(thependantid);
      break;
    default:
      thependantid.SetType(WOKBuilder_TypeModified);
      if(myactions.IsBound(thependantid)) myactions.UnBind(thependantid);
      break;
    }

  if(myentities.IsBound(theid.Name()))
    {
      myentities.UnBind(theid.Name());
    }

  return;
}

//=======================================================================
//function : Clear
//purpose  : 
//=======================================================================
void WOKBuilder_MSchema::Clear()
{
  myschema = new MS_MetaSchema();
  //MS_ClearMapOfName();

  myentities.Statistics(cout);
  cout << endl;

  myentities.Clear();
  myactions.Statistics(cout);
  cout << endl;
  myactions.Clear();
}






