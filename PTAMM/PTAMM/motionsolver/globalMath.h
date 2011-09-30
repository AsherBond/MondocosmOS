#ifndef __GLOBALMATH__H
#define __GLOBALMATH__H

#ifndef max
#define max(a,b)            (((a) > (b)) ? (a) : (b))
#endif

#ifndef min
#define min(a,b)            (((a) < (b)) ? (a) : (b))
#endif

#ifndef Z_PI
#define Z_PI	 3.1415926
#endif


inline double PowN(double x,int n)
{
	double value = x;
	for(int i=1;i<n;i++){
		value *= x;
	}
	return value;
}

#endif