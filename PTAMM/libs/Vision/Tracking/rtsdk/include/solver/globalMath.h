#ifndef __GLOBALMATH__H
#define __GLOBALMATH__H

#ifndef max
#define max(a,b)            (((a) > (b)) ? (a) : (b))
#endif

#ifndef min
#define min(a,b)            (((a) < (b)) ? (a) : (b))
#endif

//double PI	= 3.1415926;

template <class T>
inline T PowN(T x,int n)
{
	T value = x;
	for(int i=1;i<n;i++){
		value *= x;
	}
	return value;
}

template <class T>
inline T pow2(T x)
{
	return x*x;
}

#endif