#ifdef WNT
#include <WOKNT_CompareOfString.ixx>

WOKNT_CompareOfString :: WOKNT_CompareOfString () {

}  // end constructor

Standard_Boolean WOKNT_CompareOfString ::
                  IsLower (
                   const Handle( TCollection_HAsciiString )& Left,
                   const Handle( TCollection_HAsciiString )& Right
                  ) const {

 return Left -> IsLess ( Right );

}  // end WOKNT_CompareOfString :: IsLower

Standard_Boolean WOKNT_CompareOfString ::
                  IsGreater (
                   const Handle( TCollection_HAsciiString )& Left,
                   const Handle( TCollection_HAsciiString )& Right
                  ) const {

 return Left -> IsGreater ( Right );

}  // end WOKNT_CompareOfString :: IsGreater

Standard_Boolean WOKNT_CompareOfString ::
                  IsEqual (
                   const Handle( TCollection_HAsciiString )& Left,
                   const Handle( TCollection_HAsciiString )& Right
                  ) const {

 return Left -> IsSameString ( Right );

}  // end WOKNT_CompareOfString :: IsEqual

#endif
