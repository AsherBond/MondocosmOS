// File:	WOKBuilder_MSHeaderExtractor.cxx
// Created:	Tue Mar 19 20:15:33 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <MS_GenClass.hxx>
#include <MS_InstClass.hxx>
#include <MS_Pointer.hxx>
#include <MS_Alias.hxx>
#include <MS_Error.hxx>
#include <MS_Package.hxx>
#include <MS.hxx>


#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <WOKBuilder_MSHeaderExtractor.ixx>


WOKBuilder_MSHeaderExtractor :: WOKBuilder_MSHeaderExtractor (
                                 const Handle( TCollection_HAsciiString        )&      aname,
                                 const Handle( TCollection_HAsciiString        )&    ashared,
                                 const Handle( TColStd_HSequenceOfHAsciiString )& searchlist
                                ) : WOKBuilder_MSExtractor ( aname, ashared, searchlist ) {}

//=======================================================================
//function : WOKBuilder_MSHeaderExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSHeaderExtractor::WOKBuilder_MSHeaderExtractor(const Handle(TCollection_HAsciiString)& ashared,
							   const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("CPP"), ashared, searchlist)
{
}

//=======================================================================
//function : WOKBuilder_MSHeaderExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSHeaderExtractor::WOKBuilder_MSHeaderExtractor(const WOKUtils_Param& params)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("CPP"), params)
{
}

//=======================================================================
//function : WOKBuilder_MSHeaderExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSHeaderExtractor::WOKBuilder_MSHeaderExtractor(const Handle(TCollection_HAsciiString)& aname, const WOKUtils_Param& params)
  : WOKBuilder_MSExtractor(aname, params)
{
}

//=======================================================================
//function : GetTypeDepList
//purpose  : 
//=======================================================================
Handle( TColStd_HSequenceOfHAsciiString )
 WOKBuilder_MSHeaderExtractor :: GetTypeDepList (
                                  const Handle( TCollection_HAsciiString )& aname
                                 ) const {

 Standard_Integer                          i;
 Handle( MS_Type                         ) atype;
 Handle( TCollection_HAsciiString        ) astr;
 Handle( TCollection_HAsciiString        ) aName  = aname -> Token ( "@" );
 Handle( TColStd_HSequenceOfHAsciiString ) result = new TColStd_HSequenceOfHAsciiString ();
 Handle( TColStd_HSequenceOfHAsciiString ) aList  = new TColStd_HSequenceOfHAsciiString ();
 Handle( MS_MetaSchema                   ) ameta  = MSchema () -> MetaSchema ();

 result -> Append ( aName );

 if (  ameta -> IsPackage ( aName )  ) {

  WOK_TRACE {

   VerboseMsg() ( "WOK_EXTRACT" ) << "WOKBuilder_MSHeaderExtractor::ExtractionStatus"
                                << "Package not yet Implemented : out of date"
                                << endm;
  }  // end WOK_TRACE

  return result;

 }  // end if
  
 atype = ameta -> GetType ( aName );
  
 if (  atype.IsNull ()  ) {

  Handle( MS_Package ) apk = ameta -> GetPackage ( aName );
      
  if (  apk.IsNull ()  ) {

   ErrorMsg() << "WOKBuilder_MSHeaderExtractor::ExtractionStatus"
            << aName
            << " is not a known package and not a known type"
            << endm;

   return result;

  }  // end if

 }  // end if
  
 if (   atype -> IsKind (  STANDARD_TYPE( MS_Class )  )   ) {

  Handle( MS_Class ) aclass = Handle( MS_Class ) :: DownCast ( atype );
     
  if (   !aclass -> IsKind (  STANDARD_TYPE( MS_GenClass )  )   ) {

   MS :: ClassUsedTypes (ameta, aclass, aList, aList );
	  
   if (   atype -> IsKind (  STANDARD_TYPE( MS_StdClass )  )   ) {

    Handle( MS_StdClass ) msclass = Handle( MS_StdClass ) :: DownCast ( atype );
	      
    if (  !msclass -> GetMyCreator ().IsNull ()  )

     result -> Append (  MSchema () -> AssociatedEntity ( aName )  );

    if (   atype -> IsKind (  STANDARD_TYPE( MS_Error )  )   )

     result -> Append (  MSchema () -> AssociatedEntity ( aName )  );

   }  // end if
	  
   WOKTools_MapOfHAsciiString amap;
	  
   for (  i = 1; i <= aList -> Length (); ++i  ) {

    astr = aList -> Value ( i );

    if (   !strncmp (  "Handle_", aList -> Value ( i ) -> ToCString (), strlen ( "Handle_" )  )   )

     astr = astr -> SubString (  strlen ( "Handle_" ) + 1, astr -> Length ()  );

    if (  !amap.Contains ( astr )  ) {

     amap.Add ( astr );
     result -> Append ( astr );

    }  // end if

   }  // end for

  }  // end if

 } else {

  if (   atype -> IsKind (  STANDARD_TYPE( MS_Pointer )  )   ) {

   Handle( MS_Pointer ) apointer = Handle( MS_Pointer ) :: DownCast ( atype );
	
   result -> Append (  apointer -> Type ()  );

  } else {

   if (   atype -> IsKind (  STANDARD_TYPE( MS_Alias )  )   ) {

    Handle( MS_Alias ) analias = Handle( MS_Alias ) :: DownCast ( atype );
	      
    result -> Append (  analias -> Type ()  );

   }  // end if

  }  // end else

 }  // end else

 return result;

}  // end WOKBuilder_MSHeaderExtractor :: GetTypeDepList
//=======================================================================
//function : GetTypeMDate
//purpose  : 
//=======================================================================
WOKUtils_TimeStat WOKBuilder_MSHeaderExtractor::GetTypeMDate(const Handle(TCollection_HAsciiString)& aname) const
{

  if(MSchema()->MetaSchema()->IsPackage(aname))
    {
      WOKBuilder_MSActionID anid(aname, WOKBuilder_TypeModified);
      return MSchema()->GetAction(anid)->Date();
    }
  
  Handle(MS_Type) atype = MSchema()->MetaSchema()->GetType(aname);
  
  if(atype->IsKind(STANDARD_TYPE(MS_NatType)))
    {
      Handle(TCollection_HAsciiString) astr = MSchema()->AssociatedEntity(aname);

      WOKBuilder_MSActionID anid(astr, WOKBuilder_TypeModified);
      return MSchema()->GetAction(anid)->Date();
    }
  else
    {

      if(atype->IsKind(STANDARD_TYPE(MS_Class)))
	{
	  Handle(MS_Class) aclass = Handle(MS_Class)::DownCast(atype);

	  if(aclass->IsNested())
	    {
	      return GetTypeMDate(aclass->GetNestingClass());
	    }
	  else
	    {
	      if(atype->IsKind(STANDARD_TYPE(MS_StdClass)))
		{
		  Handle(MS_StdClass) msclass = Handle(MS_StdClass)::DownCast(atype);
		  
		  if(!msclass->GetMyCreator().IsNull())
		    {
		      // instantiation
		      Handle(MS_InstClass) instclass = msclass->GetMyCreator();

		      return GetTypeMDate(instclass->GenClass());
		    }
		  if(atype->IsKind(STANDARD_TYPE(MS_Error)))
		    {
		      // exception
		     Handle(TCollection_HAsciiString) astr = MSchema()->AssociatedEntity(aname);

		     WOKBuilder_MSActionID anid(astr, WOKBuilder_TypeModified);
		     return MSchema()->GetAction(anid)->Date(); 
		    }
		}

	    }
	}
      WOKBuilder_MSActionID anid(aname, WOKBuilder_TypeModified);
      return MSchema()->GetAction(anid)->Date();     
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractorID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionType WOKBuilder_MSHeaderExtractor::ExtractorID() const
{
  return WOKBuilder_HeaderExtract;
}

//=======================================================================
//function : ExtractionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSHeaderExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& anaction)
{
  Handle(TCollection_HAsciiString) astr;
  Handle(TColStd_HSequenceOfHAsciiString) aList;
  Standard_Integer i;

  WOKBuilder_MSActionID anid(anaction->Entity()->Name(), anaction->Type());

  if(!MSchema()->IsActionDefined(anid)) return WOKBuilder_OutOfDate;

  aList = GetTypeDepList(anaction->Entity()->Name());

  for(i=1; i<=aList->Length(); i++)
    {
      astr = aList->Value(i);
      
      WOKUtils_TimeStat depdate = GetTypeMDate(astr);

      WOK_TRACE {
	VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSHeaderExtractor::ExtractionStatus" 
				  << "Comparing extraction   date : " << (Standard_Integer) anaction->Date() << " of " << anaction->Entity()->Name() << endm;

	VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSHeaderExtractor::ExtractionStatus" 
				  << "with      modification date : " << (Standard_Integer) depdate << " of " << astr << endm;
      }

      if( GetTypeMDate(astr) > anaction->Date())
	{
	  WOK_TRACE {
	    VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSHeaderExtractor::ExtractionStatus" 
				      << anaction->Entity()->Name() << " is out of date compared to " << astr << endm;
	  }
	  return WOKBuilder_OutOfDate;
	}
      else
	{
	  WOK_TRACE {
	    VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSHeaderExtractor::ExtractionStatus" 
				      << anaction->Entity()->Name() << " is up to date compared to : " << astr << endm;
	  }
	}
    }
  
  WOK_TRACE {
    VerboseMsg()("WOK_EXTRACT") << "WOKBuilder_MSHeaderExtractor::ExtractionStatus" 
			      << anaction->Entity()->Name() << " is up to date" << endm;
  }

  return WOKBuilder_UpToDate;
}

