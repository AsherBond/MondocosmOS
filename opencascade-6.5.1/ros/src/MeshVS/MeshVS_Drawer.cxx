// File:  MeshVS_Drawer.cxx
// Created: Thu Nov 27 2003
// Author:  Alexander SOLOVYOV
// Copyright: Open CASCADE 2003

#include <MeshVS_Drawer.ixx>

//================================================================
// Function : Assign
// Purpose  :
//================================================================
void MeshVS_Drawer::Assign( const Handle(MeshVS_Drawer)& aDrawer )
{
  if ( !aDrawer.IsNull() )
  {
    myIntegers    = aDrawer->myIntegers;
    myDoubles     = aDrawer->myDoubles;
    myBooleans    = aDrawer->myBooleans;
    myColors      = aDrawer->myColors;
    myMaterials   = aDrawer->myMaterials;
    myAsciiString = aDrawer->myAsciiString;
  }
}

//================================================================
// Function : SetInteger
// Purpose  :
//================================================================
void MeshVS_Drawer::SetInteger( const Standard_Integer Key, const Standard_Integer Value)
{
  if ( myIntegers.IsBound ( Key ) )
    myIntegers.ChangeFind ( Key ) = Value;
  else
    myIntegers.Bind( Key, Value );
}

//================================================================
// Function : SetDouble
// Purpose  :
//================================================================
void MeshVS_Drawer::SetDouble( const Standard_Integer Key, const Standard_Real Value)
{
  if ( myDoubles.IsBound ( Key ) )
    myDoubles.ChangeFind ( Key ) = Value;
  else
    myDoubles.Bind( Key, Value );
}

//================================================================
// Function : SetBoolean
// Purpose  :
//================================================================
void MeshVS_Drawer::SetBoolean( const Standard_Integer Key, const Standard_Boolean Value)
{
  if ( myBooleans.IsBound ( Key ) )
    myBooleans.ChangeFind ( Key ) = Value;
  else
    myBooleans.Bind( Key, Value );
}

//================================================================
// Function : SetColor
// Purpose  :
//================================================================
void MeshVS_Drawer::SetColor( const Standard_Integer Key, const Quantity_Color& Value)
{
  if ( myColors.IsBound ( Key ) )
    myColors.ChangeFind ( Key ) = Value;
  else
    myColors.Bind( Key, Value );
}

//================================================================
// Function : SetMaterial
// Purpose  :
//================================================================
void MeshVS_Drawer::SetMaterial( const Standard_Integer Key, const Graphic3d_MaterialAspect& Value)
{
  if ( myMaterials.IsBound ( Key ) )
    myMaterials.ChangeFind ( Key ) = Value;
  else
    myMaterials.Bind( Key, Value );
}

//================================================================
// Function : SetMaterial
// Purpose  :
//================================================================
void MeshVS_Drawer::SetAsciiString( const Standard_Integer Key, const TCollection_AsciiString& Value)
{
  if ( myAsciiString.IsBound ( Key ) )
    myAsciiString.ChangeFind ( Key ) = Value;
  else
    myAsciiString.Bind( Key, Value );
}


//================================================================
// Function : GetInteger
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::GetInteger( const Standard_Integer Key, Standard_Integer& Value) const
{
  Standard_Boolean aRes = myIntegers.IsBound ( Key );
  if ( aRes )
    Value = myIntegers.Find ( Key );
  return aRes;
}

//================================================================
// Function : GetDouble
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::GetDouble( const Standard_Integer Key, Standard_Real& Value) const
{
  Standard_Boolean aRes = myDoubles.IsBound ( Key );
  if ( aRes )
    Value = myDoubles.Find ( Key );
  return aRes;
}

//================================================================
// Function : GetBoolean
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::GetBoolean( const Standard_Integer Key, Standard_Boolean& Value) const
{
  Standard_Boolean aRes = myBooleans.IsBound ( Key );
  if ( aRes )
    Value = myBooleans.Find ( Key );
  return aRes;
}

//================================================================
// Function : GetColor
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::GetColor( const Standard_Integer Key, Quantity_Color& Value) const
{
  Standard_Boolean aRes = myColors.IsBound ( Key );
  if ( aRes )
    Value = myColors.Find ( Key );
  return aRes;
}

//================================================================
// Function : GetMaterial
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::GetMaterial( const Standard_Integer Key, Graphic3d_MaterialAspect& Value) const
{
  Standard_Boolean aRes = myMaterials.IsBound ( Key );
  if ( aRes )
    Value = myMaterials.Find ( Key );
  return aRes;
}

//================================================================
// Function : GetAsciiSstring
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::GetAsciiString( const Standard_Integer Key, TCollection_AsciiString& Value) const
{
  Standard_Boolean aRes = myAsciiString.IsBound ( Key );
  if ( aRes )
    Value = myAsciiString.Find ( Key );
  return aRes;
}


//================================================================
// Function : RemoveInteger
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::RemoveInteger( const Standard_Integer Key )
{
  Standard_Boolean aRes = myIntegers.IsBound ( Key );
  if ( aRes )
    myIntegers.UnBind ( Key );
  return aRes;
}

//================================================================
// Function : RemoveDouble
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::RemoveDouble( const Standard_Integer Key )
{
  Standard_Boolean aRes = myDoubles.IsBound ( Key );
  if ( aRes )
    myDoubles.UnBind ( Key );
  return aRes;
}

//================================================================
// Function : RemoveBoolean
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::RemoveBoolean( const Standard_Integer Key )
{
  Standard_Boolean aRes = myBooleans.IsBound ( Key );
  if ( aRes )
    myBooleans.UnBind ( Key );
  return aRes;
}

//================================================================
// Function : RemoveColor
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::RemoveColor( const Standard_Integer Key )
{
  Standard_Boolean aRes = myColors.IsBound ( Key );
  if ( aRes )
    myColors.UnBind ( Key );
  return aRes;
}

//================================================================
// Function : RemoveMaterial
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::RemoveMaterial( const Standard_Integer Key )
{
  Standard_Boolean aRes = myMaterials.IsBound ( Key );
  if ( aRes )
    myMaterials.UnBind ( Key );
  return aRes;
}

//================================================================
// Function : RemoveAsciiString
// Purpose  :
//================================================================
Standard_Boolean MeshVS_Drawer::RemoveAsciiString( const Standard_Integer Key )
{
  Standard_Boolean aRes = myAsciiString.IsBound ( Key );
  if ( aRes )
    myAsciiString.UnBind ( Key );
  return aRes;
}

