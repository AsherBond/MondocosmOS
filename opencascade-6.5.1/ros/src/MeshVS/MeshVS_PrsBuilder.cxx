// File:	MeshVS_PrsBuilder.cxx
// Created:	Tue Sep 9 2003
// Author:	Alexander SOLOVYOV
// Copyright:	 Open CASCADE 2003

#include <MeshVS_PrsBuilder.ixx>

#include <Prs3d_Root.hxx>
#include <MeshVS_Mesh.hxx>

//================================================================
// Function : Constructor MeshVS_PrsBuilder
// Purpose  :
//================================================================
MeshVS_PrsBuilder::MeshVS_PrsBuilder ( const Handle(MeshVS_Mesh)& Parent,
                                       const MeshVS_DisplayModeFlags& Flags,
                                       const Handle(MeshVS_DataSource)& DS,
                                       const Standard_Integer Id,
                                       const MeshVS_BuilderPriority& Priority )
{
  if (Id<0 && Parent!=0 )
    myId = Parent->GetFreeId();
  else
    myId = Id;

  myParentMesh = Parent.operator->();
  myDataSource = DS;
  myDrawer = 0;

  myFlags = Flags;
  myIsExcluding = Standard_False;

  myPriority = Priority;
}

//================================================================
// Function : CustomDraw
// Purpose  :
//================================================================
void MeshVS_PrsBuilder::CustomBuild ( const Handle(Prs3d_Presentation)&,
                                      const TColStd_PackedMapOfInteger&,
                                      TColStd_PackedMapOfInteger&,
                                      const Standard_Integer ) const
{
}

//================================================================
// Function : CustomSensitiveEntity
// Purpose  :
//================================================================
Handle( SelectBasics_SensitiveEntity ) MeshVS_PrsBuilder::CustomSensitiveEntity
  ( const Handle( SelectBasics_EntityOwner )&,
    const Standard_Integer ) const
{
  return 0;
}

//================================================================
// Function : DataSource
// Purpose  :
//================================================================
Handle (MeshVS_DataSource) MeshVS_PrsBuilder::DataSource () const
{
  return myDataSource;
}

//================================================================
// Function : GetDataSource
// Purpose  :
//================================================================
Handle (MeshVS_DataSource) MeshVS_PrsBuilder::GetDataSource () const
{
  if ( myDataSource.IsNull() )
    return myParentMesh->GetDataSource();
  else
    return myDataSource;
}

//================================================================
// Function : SetDataSource
// Purpose  :
//================================================================
void MeshVS_PrsBuilder::SetDataSource ( const Handle (MeshVS_DataSource)& DS )
{
  myDataSource = DS;
}

//================================================================
// Function : GetFlags
// Purpose  :
//================================================================
Standard_Integer MeshVS_PrsBuilder::GetFlags () const
{
  return myFlags;
}

//================================================================
// Function : GetId
// Purpose  :
//================================================================
Standard_Integer MeshVS_PrsBuilder::GetId () const
{
  return myId;
}

//================================================================
// Function : TestFlags
// Purpose  :
//================================================================
Standard_Boolean MeshVS_PrsBuilder::TestFlags ( const Standard_Integer DisplayMode ) const
{
  return ( ( DisplayMode & GetFlags() ) > 0 );
}

//================================================================
// Function : SetExcluding
// Purpose  :
//================================================================
void MeshVS_PrsBuilder::SetExcluding  ( const Standard_Boolean state )
{
  myIsExcluding = state;
}

//================================================================
// Function : IsExcludingOn
// Purpose  :
//================================================================
Standard_Boolean MeshVS_PrsBuilder::IsExcludingOn () const
{
  return myIsExcluding;
}

//================================================================
// Function : GetPriority
// Purpose  :
//================================================================
Standard_Integer MeshVS_PrsBuilder::GetPriority () const
{
  return myPriority;
}

//================================================================
// Function : GetDrawer
// Purpose  :
//================================================================
Handle (MeshVS_Drawer) MeshVS_PrsBuilder::GetDrawer () const
{
  if ( myDrawer.IsNull() )
    return myParentMesh->GetDrawer();
  else
    return myDrawer;
}

//================================================================
// Function : SetDataSource
// Purpose  :
//================================================================
void MeshVS_PrsBuilder::SetDrawer ( const Handle (MeshVS_Drawer)& Dr )
{
  myDrawer = Dr;
}

//================================================================
// Function : Drawer
// Purpose  :
//================================================================
Handle (MeshVS_Drawer) MeshVS_PrsBuilder::Drawer () const
{
  return myDrawer;
}

//================================================================
// Function : SetPresentationManager
// Purpose  : Set presentation manager. This method is used by 
//            MeshVS_Mesh::Compute methodto assign presentation 
//            manager to the builder. 
//================================================================
void MeshVS_PrsBuilder::SetPresentationManager( const Handle(PrsMgr_PresentationManager3d)& thePrsMgr )
{
  myPrsMgr = thePrsMgr;
}

//================================================================
// Function : GetPresentationManager
// Purpose  : Get presentation manager
//================================================================
Handle(PrsMgr_PresentationManager3d) MeshVS_PrsBuilder::GetPresentationManager() const
{
  return myPrsMgr;
}
