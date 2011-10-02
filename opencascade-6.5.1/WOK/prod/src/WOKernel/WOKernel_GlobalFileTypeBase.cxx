

#include <TCollection_HAsciiString.hxx>

#include <WOKernel_Entity.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_FileTypeBase.hxx>

#include <WOKernel_GlobalFileTypeBase.ixx>



WOKernel_GlobalFileTypeBase::WOKernel_GlobalFileTypeBase()
{
}


Handle(WOKernel_FileTypeBase) WOKernel_GlobalFileTypeBase::GetFileTypeBase(const Handle(WOKernel_Entity)& anent)
{
  Handle(WOKernel_FileTypeBase) nullresult;
  Handle(WOKernel_Session)      asession = anent->Session();

  if(anent.IsNull()) return nullresult;
  
  Handle(TCollection_HAsciiString) baseid = new TCollection_HAsciiString;

  Handle(WOKernel_Entity) theent = anent;

  while(!theent.IsNull())
    {
      if(!asession->IsDevUnit(theent->FullName()))
	baseid->Prepend(theent->EntityCode());
      else
	{
	  const Handle(WOKernel_DevUnit)& unit = asession->GetDevUnit(theent->FullName());
	  baseid->Prepend(unit->Type());
	}
      if(theent->Nesting().IsNull())
	  theent.Nullify();
      else
	{
	  static Handle(TCollection_HAsciiString) PP = new TCollection_HAsciiString(":");
	  baseid->Prepend(PP);
	  theent = asession->GetEntity(theent->Nesting());
	}
    }

  if(mybases.IsBound(baseid)) return mybases.Find(baseid);
  else
    {
      Handle(WOKernel_FileTypeBase) abase = new WOKernel_FileTypeBase;

      abase->Load(anent->Params());

      mybases.Bind(baseid, abase);

      return abase;
    }
}
