// File:	WOKernel_BasicUnitTypes.hxx
// Created:	Tue Jun 10 15:48:12 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#ifndef WOKernel_BasicUnitTypes_HeaderFile
#define WOKernel_BasicUnitTypes_HeaderFile


#define WOKernel_IsPackage(aunit)       (aunit->TypeCode() == 'p')
#define WOKernel_IsSchema(aunit)        (aunit->TypeCode() == 's')
#define WOKernel_IsInterface(aunit)     (aunit->TypeCode() == 'i')
#define WOKernel_IsClient(aunit)        (aunit->TypeCode() == 'C'||aunit->TypeCode()=='j')
#define WOKernel_IsEngine(aunit)        (aunit->TypeCode() == 'e')
#define WOKernel_IsExecutable(aunit)    (aunit->TypeCode() == 'x')
#define WOKernel_IsNocdlpack(aunit)     (aunit->TypeCode() == 'n')
#define WOKernel_IsToolkit(aunit)       (aunit->TypeCode() == 't')
#define WOKernel_IsResource(aunit)      (aunit->TypeCode() == 'r')
#define WOKernel_IsDocumentation(aunit) (aunit->TypeCode() == 'O')
#define WOKernel_IsCCL(aunit)           (aunit->TypeCode() == 'c')
#define WOKernel_IsFrontal(aunit)       (aunit->TypeCode() == 'f')
#define WOKernel_IsDelivery(aunit)      (aunit->TypeCode() == 'd')
#define WOKernel_IsIDL(aunit)           (aunit->TypeCode() == 'I')
#define WOKernel_IsServer(aunit)        (aunit->TypeCode() == 'S')


#define WOKernel_IsCDLUnit(aunit) ((aunit->TypeCode() == 'p') || \
				   (aunit->TypeCode() == 's') || \
				   (aunit->TypeCode() == 'i') || \
				   (aunit->TypeCode() == 'C') || \
				   (aunit->TypeCode() == 'e') || \
				   (aunit->TypeCode() == 'x') || \
				   (aunit->TypeCode() == 'S') )

#endif
