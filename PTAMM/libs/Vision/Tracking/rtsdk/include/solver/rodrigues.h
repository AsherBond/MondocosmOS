#include <cmath>
#include <memory.h>
#include "WmlMatrix3.h"
#include "WmlVector3.h"
#include <cstdio>

template <class T>
T norm(T x[], int length)
{
	T t = 0.0;
	for(int i = 0; i < length; ++i)
	{
		t += (x[i]*x[i]);
	}
	return sqrt(t);
}

template <class T>
void divide(T x[], int length, T d)
{
	for(int i = 0; i < length; ++i)
	{
		x[i] /= d;
	}
}

template <class T>
Wml::Matrix3<T> rodrigues(T vrot[3], T eps = 1.0e-5)
{
	Wml::Matrix3<T> matR;
		
	T theta = norm(vrot, 3);
//	printf("theta = %f", theta);
	if (theta < eps)
	{
		matR.MakeIdentity();
	}
    else
	{
		T omega[3];
		memcpy(omega, vrot, 3*sizeof(T));
		divide(omega, 3, theta);
	 	 
		T alpha = cos(theta);
		T beta = sin(theta);
		T gamma = 1.0-cos(theta);
		Wml::Matrix3<T> omegav;
		omegav.MakeZero();

		omegav(0, 1) =  -omega[2];
		omegav(0, 2) = omega[1];
		omegav(1, 0) = omega[2];
		omegav(1, 2) = -omega[0];
		omegav(2, 0) = -omega[1];
		omegav(2, 1) = omega[0];
		
		Wml::Vector3<T> vOmega;
		vOmega[0] = omega[0];
		vOmega[1] = omega[1];
		vOmega[2] = omega[2];

		Wml::Matrix3<T> A(vOmega, vOmega);

		Wml::Matrix3<T> I;
		I.MakeIdentity();

		matR = I*alpha + omegav*beta + A*gamma;
	}
	return matR;
}

template <class T>
Wml::Matrix4<T> MakeProjectiveMatrix(T rot[3], T trans[3])
{
	Wml::Matrix4<T> P;
	
	// 	Wml::Matrix3d matrot;
	// 	matrot.FromEulerAnglesZYX(rot[2], rot[1], rot[0]);
	// 	for(int i = 0; i < 3; ++i)
	// 		for(int j = 0; j < 3; ++j)
	// 		{
	// 			P(i, j)	= matrot(i, j);
	// 		} 
	
	//	PrintMatix(matrot);
	Wml::Matrix3<T> matrot = rodrigues(rot);
	
	for(int i = 0; i < 3; ++i)
	{
		for(int j = 0; j < 3; ++j)
		{
			P(i, j)	= matrot(i, j);
		}
	}
		
	
	P(0, 3) = trans[0];
	P(1, 3) = trans[1];
	P(2, 3) = trans[2];
	
	P[3][0] = P[3][1] = P[3][2] = 0.0;
	
	P[3][3] = 1.0;
	
	return P;
}