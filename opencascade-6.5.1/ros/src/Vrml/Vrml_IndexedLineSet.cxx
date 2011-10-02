#include <Vrml_IndexedLineSet.ixx>

 Vrml_IndexedLineSet::Vrml_IndexedLineSet(const Handle(TColStd_HArray1OfInteger)& aCoordIndex, 
					  const Handle(TColStd_HArray1OfInteger)& aMaterialIndex, 
					  const Handle(TColStd_HArray1OfInteger)& aNormalIndex, 
					  const Handle(TColStd_HArray1OfInteger)& aTextureCoordIndex)
{
 myCoordIndex = aCoordIndex;
 myMaterialIndex = aMaterialIndex;
 myNormalIndex = aNormalIndex;
 myTextureCoordIndex = aTextureCoordIndex;
}

 Vrml_IndexedLineSet::Vrml_IndexedLineSet()
{
 myCoordIndex        = new TColStd_HArray1OfInteger(1,1, 0);
 myMaterialIndex     = new TColStd_HArray1OfInteger(1,1,-1);
 myNormalIndex       = new TColStd_HArray1OfInteger(1,1,-1);
 myTextureCoordIndex = new TColStd_HArray1OfInteger(1,1,-1);
}

void Vrml_IndexedLineSet::SetCoordIndex(const Handle(TColStd_HArray1OfInteger)& aCoordIndex)
{
 myCoordIndex = aCoordIndex;
}

Handle(TColStd_HArray1OfInteger) Vrml_IndexedLineSet::CoordIndex() const 
{
 return  myCoordIndex;
}

void Vrml_IndexedLineSet::SetMaterialIndex(const Handle(TColStd_HArray1OfInteger)& aMaterialIndex)
{
 myMaterialIndex = aMaterialIndex;
}

Handle(TColStd_HArray1OfInteger) Vrml_IndexedLineSet::MaterialIndex() const
{
 return  myMaterialIndex;
}

void Vrml_IndexedLineSet::SetNormalIndex(const Handle(TColStd_HArray1OfInteger)& aNormalIndex)
{
 myNormalIndex = aNormalIndex;
}

Handle(TColStd_HArray1OfInteger) Vrml_IndexedLineSet::NormalIndex() const 
{
 return  myNormalIndex;
}

void Vrml_IndexedLineSet::SetTextureCoordIndex(const Handle(TColStd_HArray1OfInteger)& aTextureCoordIndex)
{
 myTextureCoordIndex = aTextureCoordIndex;
}

Handle(TColStd_HArray1OfInteger) Vrml_IndexedLineSet::TextureCoordIndex() const 
{
 return  myTextureCoordIndex;
}

Standard_OStream& Vrml_IndexedLineSet::Print(Standard_OStream& anOStream) const 
{
 Standard_Integer i;

 anOStream  << "IndexedLineSet {" << endl;
 
 if ( myCoordIndex->Length() != 1 || myCoordIndex->Value(myCoordIndex->Lower())!=0 )
  { 
    anOStream  << "    coordIndex [" << endl << '\t';
    for ( i = myCoordIndex->Lower(); i <= myCoordIndex->Upper(); i++ )
	{
	 anOStream << myCoordIndex->Value(i);
	 if ( i < myCoordIndex->Length() )
	    anOStream  << ',';

	 if ( myCoordIndex->Value(i) == -1 )
	    anOStream  << endl << '\t';
        }
    anOStream  << ']' << endl;
  }

 if ( myMaterialIndex->Length() != 1 || myMaterialIndex->Value(myMaterialIndex->Lower())!=-1 )
  { 
    anOStream  << "    materialIndex [" << endl << '\t';
    for ( i = myMaterialIndex->Lower(); i <= myMaterialIndex->Upper(); i++ )
	{
	 anOStream << myMaterialIndex->Value(i);
	 if ( i < myMaterialIndex->Length() )
	    anOStream  << ',' ;

	 if ( myMaterialIndex->Value(i) == -1 )
  	    anOStream  << endl << '\t';
        } // End of for
     anOStream  << ']' << endl;
  }

 if ( myNormalIndex->Length() != 1 || myNormalIndex->Value(myNormalIndex->Lower())!=-1 )
  { 
    anOStream  << "    normalIndex [" << endl << '\t';
     for ( i=myNormalIndex->Lower(); i <= myNormalIndex->Upper(); i++ )
	{
	 anOStream << myNormalIndex->Value(i);
	 if ( i < myNormalIndex->Length() )
	    anOStream  << ',';

	 if ( myNormalIndex->Value(i) == -1 )
	    anOStream  << endl << '\t';
        } // End of for
     anOStream  << ']' << endl;
  }

 if ( myTextureCoordIndex->Length() != 1 || myTextureCoordIndex->Value(myTextureCoordIndex->Lower())!=-1 )
  { 
    anOStream  << "    textureCoordIndex [" << endl << '\t';
     for ( i=myTextureCoordIndex->Lower(); i <= myTextureCoordIndex->Upper(); i++ )
	{
	 anOStream << myTextureCoordIndex->Value(i);
	 if ( i < myTextureCoordIndex->Length() )
	    anOStream  << ',';

	 if ( myTextureCoordIndex->Value(i) == -1 )
	    anOStream  << endl << '\t';
        } // End of for
      anOStream  << ']' << endl;
  }
  anOStream  << '}' << endl;
 return anOStream;
}
