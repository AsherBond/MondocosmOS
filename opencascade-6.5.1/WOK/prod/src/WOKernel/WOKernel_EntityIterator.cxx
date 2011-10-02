


#include <WOKernel_EntityIterator.ixx>

WOKernel_EntityIterator::WOKernel_EntityIterator(const Handle(WOKernel_Session)& asession) 
  : myfactit(asession->myfactories), mywareit(asession->mywarehouses), myshopit(asession->myworkshops), 
    myparcit(asession->myparcels), mybenchit(asession->myworkbenches), myunitit(asession->myunits)
{
}

Standard_Boolean WOKernel_EntityIterator::More() const
{
  if(myfactit.More()) return Standard_True;
  return Standard_False;
}


const Handle(WOKernel_Entity)& WOKernel_EntityIterator::Value() const
{
  if(myunitit.More())   return myunitit.Value();
  if(mybenchit.More())  return mybenchit.Value();
  if(myparcit.More())   return myparcit.Value();
  if(myshopit.More())   return myshopit.Value();
  if(mywareit.More())   return mywareit.Value();
  if(myfactit.More())   return myfactit.Value();
  {
    static Handle(WOKernel_Entity) NULLRESULT;
    return NULLRESULT;
  }
}

const Handle(TCollection_HAsciiString)& WOKernel_EntityIterator::Key() const
{
  if(myunitit.More())   return myunitit.Key();
  if(mybenchit.More())  return mybenchit.Key();
  if(myparcit.More())   return myparcit.Key();
  if(myshopit.More())   return myshopit.Key();
  if(mywareit.More())   return mywareit.Key();
  if(myfactit.More())   return myfactit.Key();
  {
    static Handle(TCollection_HAsciiString) NULLRESULT;
    return NULLRESULT;
  }
}

void WOKernel_EntityIterator::Next()
{
  if(myunitit.More())   {myunitit.Next();return;}
  if(mybenchit.More())  {mybenchit.Next();return;}
  if(myparcit.More())   {myparcit.Next();return;}
  if(myshopit.More())   {myshopit.Next();return;}
  if(mywareit.More())   {mywareit.Next();return;}
  if(myfactit.More())   {myfactit.Next();return;}
  return;
}

