// File:	Standard_MMgrRoot.hxx
// Created:	Tue Mar 15 12:05:43 2005
// Author:	Peter KURNEV
//		<pkv@irinox>


#ifndef _Standard_MMgrRoot_HeaderFile
#define _Standard_MMgrRoot_HeaderFile

#ifndef _Standard_Address_HeaderFile
#include <Standard_Address.hxx>
#endif
#ifndef _Standard_Integer_HeaderFile
#include <Standard_Integer.hxx>
#endif
#ifndef _Standard_Macro_HeaderFile
#include <Standard_Macro.hxx>
#endif

/** 
* Root class for Open CASCADE mmemory managers.
* Defines only abstract interface functions.
*/

class Standard_MMgrRoot
{
 public:

  //! Virtual destructor; required for correct inheritance
  Standard_EXPORT virtual ~Standard_MMgrRoot();
  
  //! Allocate specified number of bytes.
  //! The actually allocated space should be rounded up to 
  //! double word size (4 bytes), as this is expected by implementation 
  //! of some classes in OCC (e.g. TCollection_AsciiString)
  Standard_EXPORT virtual Standard_Address Allocate (const Standard_Size theSize)=0;
  
  //! Reallocate previously allocated memory to contain at least theSize bytes.
  //! In case of success, aPtr should be nullified and new pointer returned.
  Standard_EXPORT virtual Standard_Address Reallocate (Standard_Address& aPtr, 
                                                       const Standard_Size theSize)=0;
  
  //! Frees previously allocated memory at specified address.
  //! The pointer is nullified.
  Standard_EXPORT virtual void Free(Standard_Address& aPtr)=0;
  
  //! Purge internally cached unused memory blocks (if any) 
  //! by releasing them to the operating system.
  //! Must return non-zero if some memory has been actually released, 
  //! or zero otherwise.
  //! 
  //! If option isDestroyed is True, this means that memory 
  //! manager is not expected to be used any more; note however 
  //! that in general case it is still possible to have calls to that 
  //! instance of memory manager after this (e.g. to free memory
  //! of static objects in OCC). Thus this option should 
  //! command the memory manager to release any cached memory
  //! to the system and not cache any more, but still remain operable...
  //!
  //! Default implementation does nothing and returns 0.
  Standard_EXPORT virtual Standard_Integer Purge(Standard_Boolean isDestroyed=Standard_False);

  //! Set reentrant mode on or off.
  //! Note: This method may be called only when no any other thread can 
  //!       access this object simultaneously.
  //! Default implementation does nothing.
  Standard_EXPORT virtual void SetReentrant(Standard_Boolean isReentrant);
};

#endif
