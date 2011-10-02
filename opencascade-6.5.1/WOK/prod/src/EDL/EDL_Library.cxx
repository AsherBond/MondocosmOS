#include <EDL_Library.ixx>

#ifdef WNT
# define TAILWORD ".dll"
# define HEADWORD ""
# define LIBLEN  0
# define TAILLEN 4
#else
# define LIBLEN  3
# define TAILLEN 3
# define LIBLEN  3
# define TAILLEN 3
# define HEADWORD "lib"
# ifdef __hpux
#  define TAILWORD ".sl"
# elif defined(__APPLE__)
#  define TAILWORD ".dylib"
#  define TAILLEN 6
# else
#  define TAILWORD ".so"
# endif
#endif


EDL_Library::EDL_Library()
{
}

EDL_Library::EDL_Library(const Standard_CString aName)
{
  if (aName != NULL) {
    char *SysName = 0L;

    myName  = new TCollection_HAsciiString(aName);

    SysName = new char[strlen(aName) + 1 + LIBLEN + TAILLEN];
#ifndef WNT
    strcpy(SysName,HEADWORD);
    strcat(SysName,myName->ToCString());
#else
    strcpy(SysName,myName->ToCString());
#endif
    strcat(SysName,TAILWORD);

    myLib.SetName(SysName);
    myLib.DlOpen(OSD_RTLD_LAZY);

    delete [] SysName;
  }
}

void EDL_Library::Assign(const EDL_Library& aLib) 
{
  if (!aLib.myName.IsNull()) {
    myName  = new TCollection_HAsciiString(aLib.myName);
  } 

  if (aLib.myLib.Name() != NULL) {    
    myLib.SetName(aLib.myLib.Name());
    myLib.DlOpen(OSD_RTLD_LAZY);
  }
}

void EDL_Library::Destroy() const
{
}

Standard_CString EDL_Library::GetName() const 
{
  return myName->ToCString();
}

OSD_Function EDL_Library::GetSymbol(const Standard_CString aName) const 
{
  OSD_Function aFunc;

  aFunc = myLib.DlSymb(aName);

  return aFunc;
}

Standard_CString EDL_Library::GetStatus() const 
{
  return myLib.DlError();
}

void EDL_Library::Close() const
{
  myLib.DlClose();
}

Standard_Integer EDL_Library::HashCode(const EDL_Library& aVar, const Standard_Integer Upper)
{
  return ::HashCode(aVar.GetName(),Upper);
}

Standard_Boolean EDL_Library::IsEqual(const EDL_Library& aTemp1, const EDL_Library& aTemp2)
{
  Standard_Boolean aResult = Standard_False;

  if (strcmp(aTemp1.GetName(),aTemp2.GetName()) == 0) {
    aResult = Standard_True;
  }

  return aResult;
}
