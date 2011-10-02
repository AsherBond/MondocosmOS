#include <MS_Exec.ixx>

MS_Exec::MS_Exec(const Handle(TCollection_HAsciiString)& anExec) : MS_GlobalEntity(anExec)
{
}

void MS_Exec::Schema(const Handle(TCollection_HAsciiString)& aSchema)
{
  mySchema = aSchema;
}

Handle(TCollection_HAsciiString) MS_Exec::Schema() const 
{
  return mySchema;
}

