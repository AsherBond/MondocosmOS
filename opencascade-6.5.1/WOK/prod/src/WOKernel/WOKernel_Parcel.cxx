// File:	WOKernel_Parcel.cxx
// Created:	Wed Jul 26 18:39:20 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <Standard_ProgramError.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>


#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Session.hxx>

#include <WOKernel_Parcel.ixx>

//=======================================================================
//function : WOKernel_Parcel
//purpose  : instantiates a parcel
//=======================================================================
WOKernel_Parcel::WOKernel_Parcel(const Handle(TCollection_HAsciiString)& aname, 
				 const Handle(WOKernel_Warehouse)& anesting) 
  : WOKernel_UnitNesting(aname, anesting)
{
}


//=======================================================================
//function : EntityCode
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Parcel::EntityCode() const 
{
  static Handle(TCollection_HAsciiString) acode = new TCollection_HAsciiString("parcel");
  return acode;
}

//=======================================================================
//function : Deliveries
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Parcel::Delivery() const
{
  return mydelivery;
}

//=======================================================================
//function : GetUnitList
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Parcel::GetUnitList() 
{
  Handle(TColStd_HSequenceOfHAsciiString) units = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) delseq;

  mydelivery = EvalParameter("Delivery", Standard_True);

  if(!mydelivery.IsNull())
    {
      Handle(WOKernel_File) allcomps = GetUnitListFile();
      allcomps->GetPath();

      Standard_Character                        typecode;
      Handle(WOKernel_DevUnit)                  unit;
      Handle(WOKernel_File)                     afile;
      Handle(TCollection_HAsciiString)          astr;

      
      ifstream astream(allcomps->Path()->Name()->ToCString(), ios::in);
      char namebuf[1024];
      
      while(astream >> typecode >> setw(1024) >> namebuf)
	{
	  astr = new TCollection_HAsciiString(namebuf);
	  
	  unit = GetDevUnit(typecode, astr);
	  
	  if(unit.IsNull() == Standard_True)
	    {
	      ErrorMsg() << "WOKernel_Parcel::GetUnitList" 
		       << "Unknown type code (" << typecode << ") in " 
		       << allcomps->Path()->Name() <<  " of " << Name() << endm;
	      Standard_ProgramError::Raise("WOKernel_Parcel::GetUnitList");
	    }
	  
	  units->Append(unit->FullName());
	  Session()->AddEntity(unit);
	  typecode = 0;
	  *namebuf = '\0';
	}
      astream.close();
    }

  return units;
}

//=======================================================================
//function : Open
//purpose  : opens parcel
//=======================================================================
void WOKernel_Parcel::Open()
{
  if(IsOpened()) return;

  Reset();

  GetParams();

  WOKernel_UnitNesting::Open();

  SetOpened();
}

//=======================================================================
//function : Close
//purpose  : closes parcel
//=======================================================================
void WOKernel_Parcel::Close()
{
  if(!IsOpened()) return;
  WOKernel_UnitNesting::Close();
  mydelivery.Nullify();
  Reset();
  SetClosed();
}


Handle(WOKernel_File) WOKernel_Parcel::GetUnitListFile() const
{
  Handle(WOKernel_File) allcomps;

  if(!mydelivery.IsNull())
    {
      
      Handle(TCollection_HAsciiString) aname = new TCollection_HAsciiString(mydelivery);
      aname->AssignCat(Params().Eval("%FILENAME_AllComponentsSuffix"));
      
      allcomps = new WOKernel_File(aname, this, GetFileType("UnitListFile"));
    }
  return allcomps;
}
