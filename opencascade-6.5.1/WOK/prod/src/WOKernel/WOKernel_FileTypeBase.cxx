// File:	WOKernel_FileTypeBase.cxx
// Created:	Wed Feb 28 23:20:18 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

#include <EDL_API.hxx>

#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_AdmFile.hxx>

#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DataMapIteratorOfDataMapOfFileType.hxx>
#include <WOKernel_FileTypeKeyWords.hxx>
#include <WOKernel_SequenceOfFileType.hxx>
#include <WOKernel_Array1OfHSequenceOfHAsciiString.hxx>
#include <WOKernel_Session.hxx>

#include <WOKernel_FileTypeBase.ixx>



//=======================================================================
//function : WOKernel_FileTypeBase
//purpose  : 
//=======================================================================
WOKernel_FileTypeBase::WOKernel_FileTypeBase()
{
}

//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKernel_FileTypeBase::Load(const WOKUtils_Param& params)
{
  Handle(TCollection_HAsciiString) astr, thestr, templname;
  Handle(WOKernel_FileType)        thetype;
  Standard_Integer i=1;

  astr =  params.Eval((Standard_CString) WOKENTITYFILELIST, Standard_False);
  
  if(astr.IsNull())
    {
      ErrorMsg() << "WOKernel_FileTypeBase::Load"
	<< "Could not evalutate parameter " << (Standard_CString) WOKENTITYFILELIST << endm;
      return;
    }

  if( !astr->IsEmpty())
    {
      thestr = astr->Token(" \t", i++);
      
      while(!thestr->IsEmpty())
	{
	  
	  if(mytypes.IsBound(thestr))
	    {
	      WarningMsg() << "WOKernel_FileTypeBase::Load" 
			 << "Redefinition of type " << thestr << endm;
	    }
	  else
	    {
	      templname = new TCollection_HAsciiString((Standard_CString) WOKENTITY);
	      templname->AssignCat("_");
	      templname->AssignCat(thestr);
	      
	      if(!params.myapi->IsDefined(templname->ToCString()))
		{
		  ErrorMsg() << "WOKernel_FileTypeBase::Load" 
		           << "Listed type " << thestr << " is not defined (" << templname << ")" << endm;
		}
	      else
		{
		  thetype = new WOKernel_FileType(thestr, params.myapi->GetTemplate(templname->ToCString()));
		  thetype->File();
		  mytypes.Bind(thestr, thetype);
		}
	    }
	  
	  thestr = astr->Token(" \t", i++);
	}
      
    }

  astr =  params.Eval((Standard_CString) WOKENTITYDIRLIST, Standard_False);
  i=1;
  
  if( !astr->IsEmpty())
    {
      thestr = astr->Token(" \t", i++);
      
      while(!thestr->IsEmpty())
	{
	  
	  if(mytypes.IsBound(thestr))
	    {
	      WarningMsg() << "WOKernel_FileTypeBase::Load" 
			 << "Redefinition of type " << thestr << endm;
	    }
	  else
	    {
	      templname = new TCollection_HAsciiString((Standard_CString) WOKENTITY);
	      templname->AssignCat("_");
	      templname->AssignCat(thestr);
	      
	      if(!params.myapi->IsDefined(templname->ToCString()))
		{
		  ErrorMsg() << "WOKernel_FileTypeBase::Load" 
		           << "Listed type " << thestr << " is not defined (" << templname << ")" << endm;
		}
	      else
		{
		  thetype = new WOKernel_FileType(thestr, params.myapi->GetTemplate(templname->ToCString()));
		  thetype->Directory();
		  mytypes.Bind(thestr, thetype);
		}
	    }
	  
	  thestr = astr->Token(" \t", i++);
	}
    }
  myneededargs = GetNeededArguments(params);
  return;
}

//=======================================================================
//function : IsType
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_FileTypeBase::IsType(const Handle(TCollection_HAsciiString)& atype) const 
{
  if(mytypes.IsBound(atype))
    return Standard_True;
  else
    return Standard_False;
}

//=======================================================================
//function : IsType
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_FileTypeBase::IsType(const Standard_CString atype) const 
{
  Handle(WOKernel_FileType) result;
  Handle(TCollection_HAsciiString) name;

  name = new TCollection_HAsciiString(atype);

  if(mytypes.IsBound(name))
    return Standard_True;
  else
    return Standard_False;
}

//=======================================================================
//function : Type
//purpose  : 
//=======================================================================
Handle(WOKernel_FileType) WOKernel_FileTypeBase::Type(const Handle(TCollection_HAsciiString)& atype) const 
{
  Handle(WOKernel_FileType) result;

  if(mytypes.IsBound(atype))
    result = mytypes.Find(atype);
  else
    {
      ErrorMsg() << "WOKernel_FileTypeBase::Type" 
	       << "Attempt to get inexistent type : " << atype << endm;
    }
  return result;
}

//=======================================================================
//function : Type
//purpose  : 
//=======================================================================
Handle(WOKernel_FileType) WOKernel_FileTypeBase::Type(const Standard_CString atype) const 
{
  Handle(WOKernel_FileType) result;
  Handle(TCollection_HAsciiString) name;

  name = new TCollection_HAsciiString(atype);

  if(mytypes.IsBound(name))
    result = mytypes.Find(name);
  else
    {
      ErrorMsg() << "WOKernel_FileTypeBase::Type" 
	       << "Attempt to get inexistent type : " << atype << endm;
    }
  return result;
}

//=======================================================================
//function : TypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_FileTypeBase::TypeName(const Handle(WOKernel_FileType)& atype) const 
{
  return atype->Name();
}

//=======================================================================
//function : GetNeededArguments
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_FileTypeBase::GetNeededArguments(const WOKUtils_Param& params) 
{
  Handle(TColStd_HSequenceOfHAsciiString) asubseq = new TColStd_HSequenceOfHAsciiString;
  Handle(TCollection_HAsciiString) astr, thestr;

  WOKernel_DataMapIteratorOfDataMapOfFileType anit(mytypes);
  WOKTools_MapOfHAsciiString                  amap;
  
  while(anit.More() == Standard_True)
    {
      params.GetArguments(anit.Value()->Template().GetName(), asubseq, amap);
      anit.Next();
    }

  astr =  params.Eval((Standard_CString) WOKENTITYPARAMLIST, Standard_False);
  
  Standard_Integer i=1;
  if( !astr->IsEmpty())
    {
      thestr = astr->Token(" \t", i++);

      while(!thestr->IsEmpty())
	{
	  if(!amap.Contains(thestr))	{asubseq->Append(thestr);amap.Add(thestr);}
	  thestr = astr->Token(" \t", i++);
	}      
    }
  
  if(params.IsSet((Standard_CString) WOKENTITYBEFOREBUID))
    params.GetArguments((Standard_CString) WOKENTITYBEFOREBUID, asubseq, amap);
  
  if(params.IsSet((Standard_CString) WOKENTITYAFTERBUILD))
    params.GetArguments((Standard_CString) WOKENTITYAFTERBUILD, asubseq, amap);
  
  if(params.IsSet((Standard_CString) WOKENTITYBEFOREDESTROY))
    params.GetArguments((Standard_CString) WOKENTITYBEFOREDESTROY, asubseq, amap);
  
  if(params.IsSet((Standard_CString) WOKENTITYAFTERDESTROY))
    params.GetArguments((Standard_CString) WOKENTITYAFTERDESTROY, asubseq, amap);
  
  myneededargs = asubseq;
  return asubseq;
}

//=======================================================================
//function : GetNeededParameters
//purpose  : Obtain from FileTypeBase the list of required arguments
//           if nesting is Null Nesting arguments are ommitted
//           idem for entity
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_FileTypeBase::GetNeededParameters(const Handle(TCollection_HAsciiString)&      nesting,
										   const Handle(TCollection_HAsciiString)&      entity,
										   const Handle(WOKernel_HSequenceOfDBMSID)&    dbmss,
										   const Handle(WOKernel_HSequenceOfStationID)& stations)

{
  Standard_Integer i,j, k, apos;
  Handle(TColStd_HSequenceOfHAsciiString)     aseq    = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString)     asubseq;
  Handle(TCollection_HAsciiString)            astr, aname, thestr;
  WOKTools_MapOfHAsciiString                  amap;

  asubseq = myneededargs;
 
  for(i=1; i<=asubseq->Length(); i++)
    {
      astr = asubseq->Value(i);
      
      if((apos = astr->Search((Standard_CString)NESTING_PREFIX)) != -1)
	{
	  if(!nesting.IsNull())
	    {
	      thestr = new TCollection_HAsciiString("%");
	      thestr->AssignCat(nesting);
	      thestr->AssignCat("_");

	      if(!strcmp(astr->ToCString(), NESTING_STATION))
		{
		  for(j=1; j<=stations->Length(); j++)
		    {
		      thestr = new TCollection_HAsciiString("%");
		      thestr->AssignCat(nesting);
		      thestr->AssignCat("_");
		      thestr->AssignCat(WOKernel_Station::GetName(stations->Value(j)));
		      if(!amap.Contains(thestr)) {aseq->Append(thestr);amap.Add(thestr);}
		    }
		}
	      else if(!strcmp(astr->ToCString(),NESTING_DBMS))
		{
		  for(j=1; j<=dbmss->Length(); j++)
		    {
		      thestr = new TCollection_HAsciiString("%");
		      thestr->AssignCat(nesting);
		      thestr->AssignCat("_");
		      thestr->AssignCat(WOKernel_DBMSystem::GetName(dbmss->Value(j)));
		      if(!amap.Contains(thestr)) {aseq->Append(thestr);amap.Add(thestr);}
		    }	      
		}
	      else if(!strcmp(astr->ToCString(),NESTING_DBMS_STATION))
		{
		  for(j=1; j<=dbmss->Length(); j++)
		    {
		      for(k=1; k<=stations->Length(); k++)
			{
			  thestr = new TCollection_HAsciiString("%");
			  thestr->AssignCat(nesting);
			  thestr->AssignCat("_");
			  thestr->AssignCat(WOKernel_DBMSystem::GetName(dbmss->Value(j)));
			  thestr->AssignCat("_");
			  thestr->AssignCat(WOKernel_Station::GetName(stations->Value(k)));
			  if(!amap.Contains(thestr))	{aseq->Append(thestr);amap.Add(thestr);}
			}
		    }
		}
	      else
		{
		  aname = new TCollection_HAsciiString(astr);
		  aname->Remove(1, strlen(NESTING_PREFIX));
		  thestr->AssignCat(aname);
		  if(!amap.Contains(thestr))	{aseq->Append(thestr);amap.Add(thestr);}
		}
	    }
	}
      else if((apos = astr->Search((Standard_CString)ENTITY_PREFIX)) != -1)
	{
	  if(!entity.IsNull())
	    {
	      thestr = new TCollection_HAsciiString("%");
	      thestr->AssignCat(entity);
	      thestr->AssignCat("_");
	      
	      if(!strcmp(astr->ToCString(), ENTITY_STATION))
		{
		  for(j=1; j<=stations->Length(); j++)
		    {
		      thestr = new TCollection_HAsciiString("%");
		      thestr->AssignCat(entity);
		      thestr->AssignCat("_");
		      thestr->AssignCat(WOKernel_Station::GetName(stations->Value(j)));
		      if(!amap.Contains(thestr))	{aseq->Append(thestr);amap.Add(thestr);}
		    }
		}
	      else if(!strcmp(astr->ToCString(),ENTITY_DBMS))
		{
		  for(j=1; j<=dbmss->Length(); j++)
		    {
		      thestr = new TCollection_HAsciiString("%");
		      thestr->AssignCat(entity);
		      thestr->AssignCat("_");
		      thestr->AssignCat(WOKernel_DBMSystem::GetName(dbmss->Value(j)));
		      if(!amap.Contains(thestr))	{aseq->Append(thestr);amap.Add(thestr);}
		    }
		}
	      else if(!strcmp(astr->ToCString(),ENTITY_DBMS_STATION))
		{
		  for(j=1; j<=dbmss->Length(); j++)
		    {
		      for(k=1; k<=stations->Length(); k++)
			{
			  thestr = new TCollection_HAsciiString("%");
			  thestr->AssignCat(entity);
			  thestr->AssignCat("_");
			  thestr->AssignCat(WOKernel_DBMSystem::GetName(dbmss->Value(j)));
			  thestr->AssignCat("_");
			  thestr->AssignCat(WOKernel_Station::GetName(stations->Value(k)));
			  if(!amap.Contains(thestr))	{aseq->Append(thestr);amap.Add(thestr);}
			}
		    }
		}
	      else
		{
		  aname = new TCollection_HAsciiString(astr);
		  aname->Remove(1, strlen(ENTITY_PREFIX));
		  thestr->AssignCat(aname);
		  if(!amap.Contains(thestr))	{aseq->Append(thestr);amap.Add(thestr);}
		}
	    }
	}
      else
	{
	  if( strcmp(astr->ToCString(), STATIONVAR) &&
	      strcmp(astr->ToCString(), DBMSVAR)    &&
	      strcmp(astr->ToCString(), NESTINGVAR) &&
	      strcmp(astr->ToCString(), ENTITYVAR)  &&
	      strcmp(astr->ToCString(), FILEVAR)  )
	    {
	      if(!amap.Contains(thestr))	{aseq->Append(thestr);amap.Add(thestr);}
	    }
	}
    }
  return aseq; 
}

//=======================================================================
//function : NeededArguments
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_FileTypeBase::NeededArguments() const
{
  return myneededargs;
}

//=======================================================================
//function : SetNeededArguments
//purpose  : 
//=======================================================================
void WOKernel_FileTypeBase::SetNeededArguments(const Handle(WOKernel_Entity)& theentity,
					       const WOKernel_DBMSID          adbms,
					       const WOKernel_StationID       astation) const
{
  Standard_Integer i;
  const Standard_Integer nestlen = strlen(NESTING_PREFIX);
  const Standard_Integer  entlen = strlen(ENTITY_PREFIX);
  TCollection_AsciiString astr;
  Handle(TCollection_HAsciiString) anesting;
  Handle(TCollection_HAsciiString) entity;
  Handle(TCollection_HAsciiString) value;
  Handle(WOKernel_Entity)          thenesting;
  Standard_CString needed;
  Standard_CString toset;
  
  if(theentity.IsNull()) return;

  entity = theentity->Name();
  
  if(!theentity->Nesting().IsNull())
    {
      thenesting = theentity->Session()->GetEntity(theentity->Nesting());
      
      if(!thenesting.IsNull())
	{
	  anesting = thenesting->Name();
	}
    }

  for(i=1; i<=myneededargs->Length(); i++)
    {
      Standard_Boolean matched = Standard_False;
      needed = myneededargs->Value(i)->ToCString();

      if(!thenesting.IsNull())
	{
	  if(!strncmp(needed, NESTING_PREFIX, nestlen))
	    {
	      matched = Standard_True;
	      astr.Clear();
	      astr.AssignCat("%");
	      astr.AssignCat(anesting->ToCString());
	      astr.AssignCat("_");
	      
	      if(!strcmp(needed, NESTING_STATION))
		{
		  // parameter %Nesting_Station needed
		  astr.AssignCat(WOKernel_Station::GetName(astation)->ToCString());
		  toset = (Standard_CString) NESTING_STATION;
		  
		}
	      else if(!strcmp(needed, NESTING_DBMS))
		{
		  // parameter %Nesting_DBMS needed
		  astr.AssignCat(WOKernel_DBMSystem::GetName(adbms)->ToCString());
		  toset = (Standard_CString) NESTING_DBMS;
		}
	      else if(!strcmp(needed, NESTING_DBMS_STATION))
		{
		  // parameter %Nesting_DBMS_STATION needed
		  astr.AssignCat(WOKernel_DBMSystem::GetName(adbms)->ToCString());
		  astr.AssignCat("_");
		  astr.AssignCat(WOKernel_Station::GetName(astation)->ToCString());
		  toset = (Standard_CString) NESTING_DBMS_STATION;
		}
	      else
		{
		  // parameter %Nesting_?????? needed
		  TCollection_AsciiString suffix(needed);
		  Standard_Integer pos = suffix.Location(1, '_', 1, suffix.Length());
		  
		  if(pos>2)
		    {
		      suffix = suffix.Split(pos);
		    }
		  astr.AssignCat(suffix);
		  
		  toset = needed;
		}
	      
	  
	      value = thenesting->Params().Eval(astr.ToCString(), Standard_False);
	      if(value.IsNull())
		{
		  ErrorMsg() << "WOKernel_FileTypeBase::SetNeededArguments"
		    << "Needed parameter : " << astr.ToCString() << " not setted" << endm;
		}
	      else
		{
		  theentity->Params().Set(toset, value->ToCString());
		}
	    }
	}
      if(!matched)
	{
	  if(!strncmp(needed, ENTITY_PREFIX, entlen))
	    {
	      matched = Standard_True;
	      astr.Clear();
	      astr.AssignCat("%");
	      astr.AssignCat(entity->ToCString());
	      astr.AssignCat("_");
	      
	      if(!strcmp(needed, ENTITY_STATION))
		{
		  // parameter %ENTITY_Station needed
		  astr.AssignCat(WOKernel_Station::GetName(astation)->ToCString());
		  toset = (Standard_CString) ENTITY_STATION;
		  
		}
	      else if(!strcmp(needed, ENTITY_DBMS))
		{
		  // parameter %ENTITY_DBMS needed
		  astr.AssignCat(WOKernel_DBMSystem::GetName(adbms)->ToCString());
		  toset = (Standard_CString) ENTITY_DBMS;
		}
	      else if(!strcmp(needed, ENTITY_DBMS_STATION))
		{
		  // parameter %ENTITY_DBMS_STATION needed
		  astr.AssignCat(WOKernel_DBMSystem::GetName(adbms)->ToCString());
		  astr.AssignCat("_");
		  astr.AssignCat(WOKernel_Station::GetName(astation)->ToCString());
		  toset = (Standard_CString) ENTITY_DBMS_STATION;
		}
	      else
		{
		  // parameter %ENTITY_?????? needed
		  TCollection_AsciiString suffix(needed);
		  Standard_Integer pos = suffix.Location(1, '_', 1, suffix.Length());
		  
		  if(pos>2)
		    {
		      suffix = suffix.Split(pos);
		    }
		  astr.AssignCat(suffix);
		  
		  toset = needed;
		}
	      
	      
	      value = theentity->Params().Eval(astr.ToCString(), Standard_False);
	      if(value.IsNull())
		{
		  ErrorMsg() << "WOKernel_FileTypeBase::SetNeededArguments"
		    << "Needed parameter : " << astr.ToCString() << " not setted" << endm;
		}
	      else
		{
		  theentity->Params().Set(toset, value->ToCString());
		}
	    }
	}
      if(!matched)
	{
	  if(!strcmp(needed, STATIONVAR))
	    {
	      theentity->Params().Set((Standard_CString) STATIONVAR, WOKernel_Station::GetName(astation)->ToCString());
	    }
	  else if(!strcmp(needed, DBMSVAR))
	    {
	      theentity->Params().Set((Standard_CString) DBMSVAR,  WOKernel_DBMSystem::GetName(adbms)->ToCString());
	    }
	  else if(!strcmp(needed, ENTITYVAR))
	    {
	      theentity->Params().Set((Standard_CString) ENTITYVAR, entity->ToCString());
	    }
	  else if(!strcmp(needed, NESTINGVAR))
	    {
	      theentity->Params().Set((Standard_CString) NESTINGVAR, anesting->ToCString());
	    }
	}
    }
}

//=======================================================================
//function : GetDirectories
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) 
  WOKernel_FileTypeBase::GetDirectories(const Handle(WOKernel_Entity)& Theentity,
					const Handle(WOKernel_HSequenceOfDBMSID)& dbmss,
					const Handle(WOKernel_HSequenceOfStationID)& stations,
					const Standard_Boolean hasentity)
  const
{
  WOKTools_MapOfHAsciiString amap;
  Standard_Integer i,j, k;
  Handle(TCollection_HAsciiString) dollars = new TCollection_HAsciiString("$$$$$$$$$$$$$$$$$");
  Handle(TCollection_HAsciiString) theunit, thenesting, apath;
  Handle(TColStd_HSequenceOfHAsciiString) result;
  Handle(WOKernel_FileType) thetype;

  if(Theentity.IsNull()) return result;

  WOKernel_SequenceOfFileType  nothingdep, stationdep, dbmsdep, bothdep;

  Handle(TCollection_HAsciiString) anesting, aunit = Theentity->Name();
  Handle(WOKernel_Entity)          nesting;

  if(!Theentity->Nesting().IsNull())
    {
      nesting = Theentity->Session()->GetEntity(Theentity->Nesting());
      
      if(!nesting.IsNull())
	{
	  anesting = nesting->Name();
	}
    }

  if(anesting.IsNull()) thenesting = dollars;
  else                  thenesting = anesting;

  if(aunit.IsNull()) theunit = dollars;
  else               theunit = aunit;
  

  if(!dbmss->Length())
    {
      ErrorMsg() << "WOKernel_FileTypeBase::GetDirectories" 
	       << "Valid DBMS systems can't be empty." << endm;
      return result;
    }
  
  if(!stations->Length())
    {
      ErrorMsg() << "WOKernel_FileTypeBase::GetDirectories" 
	       << "Valid Stations can't be empty." << endm;
      return result;
    }

  result = new TColStd_HSequenceOfHAsciiString;

  WOKernel_DataMapIteratorOfDataMapOfFileType anit(mytypes);

  while(anit.More())
    {
      thetype = anit.Value();

      if(((hasentity && thetype->IsEntityDependent()) || !hasentity) &&
	 ((thetype->IsFileDependent() && thetype->IsFile()) || thetype->IsDirectory()))
	{
	  if(thetype->IsStationDependent() && thetype->IsDBMSDependent())
	    {
	      bothdep.Append(thetype);
	    }
	  else if(thetype->IsStationDependent())
	    {
	      stationdep.Append(thetype);
	    }
	  else if(thetype->IsDBMSDependent())
	    {
	      dbmsdep.Append(thetype);
	    }
	  else
	    {
	      nothingdep.Append(thetype);
	    }
	}

      anit.Next();
    }

  SetNeededArguments(Theentity, dbmss->Value(1), stations->Value(1));

  for(i=1; i<=nothingdep.Length(); i++)
    {
      apath = nothingdep.Value(i)->GetDirectory(Theentity->Params());

      if(!apath.IsNull())
	{
	  if(!amap.Contains(apath))
	    {
	      amap.Add(apath);
	      result->Append(apath);
	    }
	}
    }

  for(i=1; i<=dbmsdep.Length(); i++)
    {
      for(j=1; j<=dbmss->Length(); j++)
	{
	  SetNeededArguments(Theentity, dbmss->Value(j), stations->Value(1));
	  
	  apath = dbmsdep.Value(i)->GetDirectory(Theentity->Params());
	  
	  if(!apath.IsNull())
	    {
	      if(!amap.Contains(apath))
		{
		  amap.Add(apath);
		  result->Append(apath);
		}
	    }
	}
    }

  for(i=1; i<=stationdep.Length(); i++)
    {
      for(j=1; j<=stations->Length(); j++)
	{
	  SetNeededArguments(Theentity, dbmss->Value(1), stations->Value(j));
	  
	  apath = stationdep.Value(i)->GetDirectory(Theentity->Params());
	  
	  if(!apath.IsNull())
	    {
	      if(!amap.Contains(apath))
		{
		  amap.Add(apath);
		  result->Append(apath);
		}
	    }
	}
    }

  for(i=1; i<=bothdep.Length(); i++)
    {
      for(j=1; j<=dbmss->Length(); j++)
	{ 
	  for(k=1; k<=stations->Length(); k++)
	    {
	      SetNeededArguments(Theentity, dbmss->Value(j), stations->Value(k));
	  
	      apath = bothdep.Value(i)->GetDirectory(Theentity->Params());
	      
	      if(!apath.IsNull())
		{
		  if(!amap.Contains(apath))
		    {
		      amap.Add(apath);
		      result->Append(apath);
		    }
		}
	    }
	}
    }

  Standard_Integer MaxLen = 0 ;

  for(i=1; i<=result->Length(); i++)
    {
      if(result->Value(i)->Length() > MaxLen) MaxLen = result->Value(i)->Length();
    }

  WOKernel_Array1OfHSequenceOfHAsciiString pathtab(1, MaxLen);
  Standard_Integer len;

  for(i=1; i<=result->Length(); i++)
    {
      len = result->Value(i)->Length();

      if(pathtab(len).IsNull()) pathtab(len) = new TColStd_HSequenceOfHAsciiString;

      pathtab(len)->Append(result->Value(i));
    }

  Handle(TColStd_HSequenceOfHAsciiString) TheResult = new TColStd_HSequenceOfHAsciiString;

  for(len=1; len<=MaxLen; len++)
    {
      if(!pathtab(len).IsNull())
	{
	  Handle(TColStd_HSequenceOfHAsciiString) lenseq = pathtab(len);
	  for(i=1; i<=lenseq->Length(); i++)
	    {
	      TheResult->Append(lenseq->Value(i));
	    }
	}
    }

  
  // On ferme/reouvre afin de retrouver notre bonne station
  //Theentity->Close();
  //Theentity->Open();

  return TheResult;
}

//=======================================================================
//function : GetDirectories
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_FileTypeBase::GetDirectories(const Handle(WOKernel_Entity)& Theentity,
									      const Handle(WOKernel_HSequenceOfDBMSID)& dbmss,
									      const Handle(WOKernel_HSequenceOfStationID)& stations,
									      const Standard_Boolean getNestingdir,
									      const Standard_Boolean getEntitydir,
									      const Standard_Boolean getNestingAndEntitydir,
									      const Standard_Boolean getDbmsdir,
									      const Standard_Boolean getStationsdir,
									      const Standard_Boolean getStationsAndDbmsdir,
									      const Standard_Boolean getIndependentdir) const
{
  WOKTools_MapOfHAsciiString amap;
  Standard_Integer i,j, k;
  Handle(TCollection_HAsciiString) dollars = new TCollection_HAsciiString("$$$$$$$$$$$$$$$$$");
  Handle(TCollection_HAsciiString) theunit, thenesting, apath;
  Handle(TColStd_HSequenceOfHAsciiString) result;
  Handle(WOKernel_FileType) thetype;

  if(Theentity.IsNull()) return result;

  WOKernel_SequenceOfFileType stationdep, dbmsdep, stationAnddbmsdep, independent;

  Handle(TCollection_HAsciiString) anesting, aunit = Theentity->Name();
  Handle(WOKernel_Entity)          Thenesting;

  if(!Theentity->Nesting().IsNull())
    {
      Thenesting = Theentity->Session()->GetEntity(Theentity->Nesting());
      
      if(!Thenesting.IsNull())
	{
	  anesting = Thenesting->Name();
	}
    }

  
  if(anesting.IsNull()) thenesting = dollars;
  else                  thenesting = anesting;

  if(aunit.IsNull()) theunit = dollars;
  else               theunit = aunit;
  

  if(!dbmss->Length())
    {
      ErrorMsg() << "WOKernel_FileTypeBase::GetDirectories" 
	       << "Valid DBMS systems can't be empty." << endm;
      return result;
    }
  
  if(!stations->Length())
    {
      ErrorMsg() << "WOKernel_FileTypeBase::GetDirectories" 
	       << "Valid Stations can't be empty." << endm;
      return result;
    }

  result = new TColStd_HSequenceOfHAsciiString;

  WOKernel_DataMapIteratorOfDataMapOfFileType anit(mytypes);

  Standard_Boolean add;

  while(anit.More())
    {
      thetype = anit.Value();

      add = Standard_False;

      if (thetype->IsEntityDependent() && thetype->IsNestingDependent() && getNestingAndEntitydir) 
	add = Standard_True;
      
      else if (thetype->IsNestingDependent() && getNestingdir) 
	add = Standard_True;
      
      else if (thetype->IsEntityDependent() && getEntitydir) 
	add = Standard_True;
      
      else if (!thetype->IsEntityDependent() && !thetype->IsNestingDependent() && getIndependentdir)
	add = Standard_True;
      
      if (add)
	{
	  if(thetype->IsStationDependent() && thetype->IsDBMSDependent())
	    stationAnddbmsdep.Append(thetype);
	  
	  else if(thetype->IsStationDependent())
	    stationdep.Append(thetype);
	  
	  else if(thetype->IsDBMSDependent())
	    dbmsdep.Append(thetype);
	  
	  else
	    independent.Append(thetype);
	}

      anit.Next();
    }

  SetNeededArguments(Theentity, dbmss->Value(1), stations->Value(1));

  if (getDbmsdir)
    {
      for(i=1; i<=dbmsdep.Length(); i++)
	{
	  for(j=1; j<=dbmss->Length(); j++)
	    {
	      SetNeededArguments(Theentity, dbmss->Value(j), stations->Value(1));
	      
	      apath = dbmsdep.Value(i)->GetDirectory(Theentity->Params());
	      
	      if(!apath.IsNull())
		{
		  if(!amap.Contains(apath))
		    {
		      amap.Add(apath);
		      result->Append(apath);
		    }
		}
	    }
	}
    }

  if(getStationsdir)
    {
      for(i=1; i<=stationdep.Length(); i++)
	{
	  for(j=1; j<=stations->Length(); j++)
	    {
	      SetNeededArguments(Theentity, dbmss->Value(1), stations->Value(j));
	      
	      apath = stationdep.Value(i)->GetDirectory(Theentity->Params());
	      
	      if(!apath.IsNull())
		{
		  if(!amap.Contains(apath))
		    {
		      amap.Add(apath);
		      result->Append(apath);
		    }
		}
	    }
	}
    }
  
  if(getStationsAndDbmsdir)
    {
      for(i=1; i<=stationAnddbmsdep.Length(); i++)
	{
	  for(j=1; j<=dbmss->Length(); j++)
	    { 
	      for(k=1; k<=stations->Length(); k++)
		{
		  SetNeededArguments(Theentity, dbmss->Value(j), stations->Value(k));
		  
		  apath = stationAnddbmsdep.Value(i)->GetDirectory(Theentity->Params());
		  
		  if(!apath.IsNull())
		    {
		      if(!amap.Contains(apath))
			{
			  amap.Add(apath);
			  result->Append(apath);
			}
		    }
		}
	    }
	}
    }
  
  if(getIndependentdir)
    {
      for(i=1; i<=independent.Length(); i++)
	{
	  apath = independent.Value(i)->GetDirectory(Theentity->Params());
	  
	  if(!apath.IsNull())
	    {
	      if(!amap.Contains(apath))
		{
		  amap.Add(apath);
		  result->Append(apath);
		}
	    }
	}
    }

  Standard_Integer MaxLen = 0 ;

  for(i=1; i<=result->Length(); i++)
    {
      if(result->Value(i)->Length() > MaxLen) MaxLen = result->Value(i)->Length();
    }

  WOKernel_Array1OfHSequenceOfHAsciiString pathtab(1, MaxLen);
  Standard_Integer len;

  for(i=1; i<=result->Length(); i++)
    {
      len = result->Value(i)->Length();

      if(pathtab(len).IsNull()) pathtab(len) = new TColStd_HSequenceOfHAsciiString;

      pathtab(len)->Append(result->Value(i));
    }

  Handle(TColStd_HSequenceOfHAsciiString) TheResult = new TColStd_HSequenceOfHAsciiString;

  for(len=1; len<=MaxLen; len++)
    {
      if(!pathtab(len).IsNull())
	{
	  Handle(TColStd_HSequenceOfHAsciiString) lenseq = pathtab(len);
	  for(i=1; i<=lenseq->Length(); i++)
	    {
	      TheResult->Append(lenseq->Value(i));
	    }
	}
    }

  // On ferme/reouvre afin de retrouver notre bonne station
  //Theentity->Close();
  //Theentity->Open();

  return TheResult;
}


//=======================================================================
//function : GetFiles
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_FileTypeBase::GetFiles(const Handle(WOKernel_Entity)& Theentity,
									const Handle(WOKernel_HSequenceOfDBMSID)& dbmss,
									const Handle(WOKernel_HSequenceOfStationID)& stations,
//									const Standard_Boolean hasentity)
									const Standard_Boolean )
     const
{
  WOKTools_MapOfHAsciiString amap;
  Standard_Integer i,j, k;
  Handle(TCollection_HAsciiString) dollars = new TCollection_HAsciiString("$$$$$$$$$$$$$$$$$");
  Handle(TCollection_HAsciiString) theunit, thenesting, apath;
  Handle(TColStd_HSequenceOfHAsciiString) result;
  Handle(WOKernel_FileType) thetype;

  if(Theentity.IsNull()) return result;

  WOKernel_SequenceOfFileType  nothingdep, stationdep, dbmsdep, bothdep;

  Handle(TCollection_HAsciiString) anesting, aunit = Theentity->Name();
  Handle(WOKernel_Entity) Thenesting;

  if(!Theentity->Nesting().IsNull())
    {
      Thenesting = Theentity->Session()->GetEntity(Theentity->Nesting());
      
      if(!Thenesting.IsNull())
	{
	  anesting = Thenesting->Name();
	}
    }
  
  if(anesting.IsNull()) thenesting = dollars;
  else                  thenesting = anesting;

  if(aunit.IsNull()) theunit = dollars;
  else               theunit = aunit;
  

  if(!dbmss->Length())
    {
      ErrorMsg() << "WOKernel_FileTypeBase::GetDirectories" 
	       << "Valid DBMS systems can't be empty." << endm;
      return result;
    }
  
  if(!stations->Length())
    {
      ErrorMsg() << "WOKernel_FileTypeBase::GetDirectories" 
	       << "Valid Stations can't be empty." << endm;
      return result;
    }

  result = new TColStd_HSequenceOfHAsciiString;

  WOKernel_DataMapIteratorOfDataMapOfFileType anit(mytypes);

  while(anit.More())
    {
      thetype = anit.Value();
      if(thetype->IsStationDependent() && thetype->IsDBMSDependent())
	{
	  bothdep.Append(thetype);
	}
      else if(thetype->IsStationDependent())
	{
	  stationdep.Append(thetype);
	}
      else if(thetype->IsDBMSDependent())
	{
	  dbmsdep.Append(thetype);
	}
      else
	{
	  nothingdep.Append(thetype);
	}

      anit.Next();
    }

  SetNeededArguments(Theentity, dbmss->Value(1), stations->Value(1));

  for(i=1; i<=nothingdep.Length(); i++)
    {
      apath = nothingdep.Value(i)->GetFile(Theentity->Params());

      if(!apath.IsNull())
	{
	  if(!amap.Contains(apath))
	    {
	      amap.Add(apath);
	      result->Append(apath);
	    }
	}
    }

  for(i=1; i<=dbmsdep.Length(); i++)
    {
      for(j=1; j<=dbmss->Length(); j++)
	{
	  SetNeededArguments(Theentity, dbmss->Value(j), stations->Value(1));
	  
	  apath = dbmsdep.Value(i)->GetFile(Theentity->Params());
	  
	  if(!apath.IsNull())
	    {
	      if(!amap.Contains(apath))
		{
		  amap.Add(apath);
		  result->Append(apath);
		}
	    }
	}
    }

  for(i=1; i<=stationdep.Length(); i++)
    {
      for(j=1; j<=stations->Length(); j++)
	{
	  SetNeededArguments(Theentity, dbmss->Value(1), stations->Value(j));
	  
	  apath = stationdep.Value(i)->GetFile(Theentity->Params());
	  
	  if(!apath.IsNull())
	    {
	      if(!amap.Contains(apath))
		{
		  amap.Add(apath);
		  result->Append(apath);
		}
	    }
	}
    }

  for(i=1; i<=bothdep.Length(); i++)
    {
      for(j=1; j<=dbmss->Length(); j++)
	{ 
	  for(k=1; k<=stations->Length(); k++)
	    {
	      SetNeededArguments(Theentity, dbmss->Value(j), stations->Value(k));
	  
	      apath = bothdep.Value(i)->GetFile(Theentity->Params());
	      
	      if(!apath.IsNull())
		{
		  if(!amap.Contains(apath))
		    {
		      amap.Add(apath);
		      result->Append(apath);
		    }
		}
	    }
	}
    }
  // On ferme/reouvre afin de retrouver notre bonne station
  //Theentity->Close();
  //Theentity->Open();

  return result;
}

//=======================================================================
//function : TypeIterator
//purpose  : 
//=======================================================================
WOKernel_FileTypeIterator WOKernel_FileTypeBase::TypeIterator() const
{
  WOKernel_FileTypeIterator theit(mytypes);

  return theit;
}
