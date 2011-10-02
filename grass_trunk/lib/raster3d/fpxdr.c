#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>
#include <rpc/types.h>
#include <rpc/xdr.h>

#include <grass/raster.h>

#include "raster3d_intern.h"

/*---------------------------------------------------------------------------*/

int Rast3d_is_xdr_null_num(const void *num, int isFloat)
{
    static const char null_bytes[8] = {
	0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
    };

    return memcmp(num, null_bytes, isFloat ? 4 : 8) == 0;
}

/*---------------------------------------------------------------------------*/

int Rast3d_is_xdr_null_float(const float *f)
{
    return Rast3d_is_xdr_null_num(f, 1);
}

/*---------------------------------------------------------------------------*/

int Rast3d_is_xdr_null_double(const double *d)
{
    return Rast3d_is_xdr_null_num(d, 0);
}

/*---------------------------------------------------------------------------*/

void Rast3d_set_xdr_null_num(void *num, int isFloat)
{
    static const char null_bytes[8] = {
	0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
    };

    memcpy(num, null_bytes, isFloat ? 4 : 8);
}

/*---------------------------------------------------------------------------*/

void Rast3d_set_xdr_null_double(double *d)
{
    Rast3d_set_xdr_null_num(d, 0);
}

/*---------------------------------------------------------------------------*/

void Rast3d_set_xdr_null_float(float *f)
{
    Rast3d_set_xdr_null_num(f, 1);
}

/*---------------------------------------------------------------------------*/

XDR xdrEncodeStream, xdrDecodeStream;	/* xdr support structures */

int Rast3d_init_fp_xdr(RASTER3D_Map * map, int misuseBytes)



 /* nof addtl bytes allocated for the xdr array so that */
		      /* the array can also be (mis)used for other purposes */
{
    int doAlloc;

    doAlloc = 0;

    if (xdr == NULL) {
	xdrLength = map->tileSize * RASTER3D_MAX(map->numLengthExtern,
					    map->numLengthIntern) +
	    misuseBytes;
	xdr = Rast3d_malloc(xdrLength);
	if (xdr == NULL) {
	    Rast3d_error("Rast3d_init_fp_xdr: error in Rast3d_malloc");
	    return 0;
	}

	doAlloc = 1;
    }
    else if (map->tileSize * RASTER3D_MAX(map->numLengthExtern,
				     map->numLengthIntern) + misuseBytes
	     > xdrLength) {
	xdrLength = map->tileSize * RASTER3D_MAX(map->numLengthExtern,
					    map->numLengthIntern) +
	    misuseBytes;
	xdr = Rast3d_realloc(xdr, xdrLength);
	if (xdr == NULL) {
	    Rast3d_error("Rast3d_init_fp_xdr: error in Rast3d_realloc");
	    return 0;
	}

	doAlloc = 1;
    }

    if (doAlloc) {
	xdrmem_create(&(xdrEncodeStream), xdr, (u_int) xdrLength, XDR_ENCODE);
	xdrmem_create(&(xdrDecodeStream), xdr, (u_int) xdrLength, XDR_DECODE);
    }

    return 1;
}

/*---------------------------------------------------------------------------*/

static void *xdrTmp;
static int dstType, srcType, type, externLength, eltLength, isFloat, useXdr;
static int (*xdrFun) ();
static XDR *xdrs;
static double tmpValue, *tmp;

int Rast3d_init_copy_to_xdr(RASTER3D_Map * map, int sType)
{
    xdrTmp = xdr;
    useXdr = map->useXdr;
    srcType = sType;

    if (map->useXdr == RASTER3D_USE_XDR) {
	if (!xdr_setpos(&(xdrEncodeStream), 0)) {
	    Rast3d_error("Rast3d_InitCopyToXdr: positioning xdr failed");
	    return 0;
	}
	xdrs = &(xdrEncodeStream);
    }

    type = map->type;
    isFloat = (type == FCELL_TYPE);
    externLength = Rast3d_extern_length(type);
    eltLength = Rast3d_length(srcType);
    if (isFloat)
	xdrFun = xdr_float;
    else
	xdrFun = xdr_double;
    tmp = &tmpValue;

    return 1;
}

/*---------------------------------------------------------------------------*/

int Rast3d_copy_to_xdr(const void *src, int nofNum)
{
    int i;

    if (useXdr == RASTER3D_NO_XDR) {
	Rast3d_copy_values(src, 0, srcType, xdrTmp, 0, type, nofNum);
	xdrTmp = G_incr_void_ptr(xdrTmp, nofNum * Rast3d_extern_length(type));
	return 1;
    }

    for (i = 0; i < nofNum; i++, src = G_incr_void_ptr(src, eltLength)) {

	if (Rast3d_is_null_value_num(src, srcType)) {
	    Rast3d_set_xdr_null_num(xdrTmp, isFloat);
	    if (!xdr_setpos(xdrs, xdr_getpos(xdrs) + externLength)) {
		Rast3d_error("Rast3d_copy_to_xdr: positioning xdr failed");
		return 0;
	    }
	}
	else {
	    if (type == srcType) {
		if (xdrFun(xdrs, src) < 0) {
		    Rast3d_error("Rast3d_copy_to_xdr: writing xdr failed");
		    return 0;
		}
	    }
	    else {
		if (type == FCELL_TYPE)
		    *((float *)tmp) = (float)*((double *)src);
		else
		    *((double *)tmp) = (double)*((float *)src);
		if (xdrFun(xdrs, tmp) < 0) {
		    Rast3d_error("Rast3d_copy_to_xdr: writing xdr failed");
		    return 0;
		}
	    }
	}

	xdrTmp = G_incr_void_ptr(xdrTmp, externLength);
    }

    return 1;
}

/*---------------------------------------------------------------------------*/

int Rast3d_init_copy_from_xdr(RASTER3D_Map * map, int dType)
{
    xdrTmp = xdr;
    useXdr = map->useXdr;
    dstType = dType;

    if (useXdr == RASTER3D_USE_XDR) {
	if (!xdr_setpos(&(xdrDecodeStream), 0)) {
	    Rast3d_error("Rast3d_init_copy_from_xdr: positioning xdr failed");
	    return 0;
	}
	xdrs = &(xdrDecodeStream);
    }

    type = map->type;
    isFloat = (type == FCELL_TYPE);
    externLength = Rast3d_extern_length(type);
    eltLength = Rast3d_length(dstType);
    if (isFloat)
	xdrFun = xdr_float;
    else
	xdrFun = xdr_double;
    tmp = &tmpValue;

    return 1;
}

/*---------------------------------------------------------------------------*/

int Rast3d_copy_from_xdr(int nofNum, void *dst)
{
    int i;

    if (useXdr == RASTER3D_NO_XDR) {
	Rast3d_copy_values(xdrTmp, 0, type, dst, 0, dstType, nofNum);
	xdrTmp = G_incr_void_ptr(xdrTmp, nofNum * Rast3d_extern_length(type));
	return 1;
    }

    for (i = 0; i < nofNum; i++, dst = G_incr_void_ptr(dst, eltLength)) {

	if (Rast3d_is_xdr_null_num(xdrTmp, isFloat)) {
	    Rast3d_set_null_value(dst, 1, dstType);
	    if (!xdr_setpos(xdrs, xdr_getpos(xdrs) + externLength)) {
		Rast3d_error("Rast3d_copy_from_xdr: positioning xdr failed");
		return 0;
	    }
	}
	else {
	    if (type == dstType) {
		if (xdrFun(xdrs, dst) < 0) {
		    Rast3d_error("Rast3d_copy_from_xdr: reading xdr failed");
		    return 0;
		}
	    }
	    else {
		if (xdrFun(xdrs, tmp) < 0) {
		    Rast3d_error("Rast3d_copy_from_xdr: reading xdr failed");
		    return 0;
		}
		if (type == FCELL_TYPE)
		    *((double *)dst) = (double)*((float *)tmp);
		else
		    *((float *)dst) = (float)*((double *)tmp);
	    }
	}

	xdrTmp = G_incr_void_ptr(xdrTmp, externLength);
    }

    return 1;
}
