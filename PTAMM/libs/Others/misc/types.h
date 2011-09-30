#ifndef byte
	typedef unsigned char byte;
#endif

#ifndef delete_safe
#define delete_safe(_p) {if(_p){ delete _p; _p = NULL;}}
#endif

#ifndef delete_safe_array
#define delete_safe_array(_p) {if(_p){delete []_p; _p = NULL;}}
#endif

//void ShowDib256(CDC *pDC, byte* pimage, int width, int height);
