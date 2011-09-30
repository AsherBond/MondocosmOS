#ifndef __MAGICMATHUTIL__H
#define	__MAGICMATHUTIL__H
#include "WmlQuaternion.h"
#include "WmlMatrix4.h"

void PrintMatix(Wml::Matrix3<double>& RotateMatrix);
void PrintMatix(Wml::Matrix4<double>& pM);
void PrintVector(Wml::Vector3d& v);

void RayToRotationXY(double x,double y ,double z,Wml::Matrix3<double>& RotateMatrix);
void Matrix3RotateWithAngle(Wml::Matrix3<double>& RotateMatrix,double x,double y,double z,
							double theta);
void AxbRotateToMatrix(double x,double y,double z,double theta,
					   Wml::Matrix3<double>& RotateMatrix);

void GetRotFromTransform(const Wml::Matrix4d& transformM,Wml::Matrix3d& rotM);

//Wml::Matrix3d GetRotFromTransform(const Wml::Matrix4d& transformM);

void GetTranslateFromTransform(const Wml::Matrix4d& transformM,Wml::Vector3d& translateGM);
//Wml::Vector3d	GetTranslateFromTransform(const Wml::Matrix4d& transformM);

//void TransformFromRotAndTranslate(Wml::Matrix4d& transformM,const Wml::Matrix3d& rotM,
//				  const Wml::Vector3d& translateGM);
Wml::Matrix4d TransformFromRotAndTranslate(const Wml::Matrix3d& rotM,
										  const Wml::Vector3d& translateGM);
void RQ_Decomposition(const Wml::Matrix3d& A, Wml::Matrix3d& R, Wml::Matrix3d& Q);

void ObjTransformToViewTransform(const Wml::Matrix4d& objTransform,Wml::Matrix4d& viewTransform);


//////////////////////////////////////////////////////////////////////////
/// template operations.
template<class T>
Wml::Matrix3<T> GetRotFromTransform(const Wml::Matrix4<T>& transformM)
{
	int i,j;
	Wml::Matrix3<T> rotM;

	for(i=0;i<3;i++)
		for(j=0;j<3;j++){
			rotM(i,j)=transformM(i,j);
		}
		return rotM;
}

template<class T>
Wml::Vector3<T>	GetTranslateFromTransform(const Wml::Matrix4<T>& transformM)
{
	int i;
	Wml::Vector3<T>	translateGM;

	for(i=0;i<3;i++)
		translateGM[i]=transformM(i,3);

	return translateGM;
}

template<class T>
void TransformFromRotAndTranslate(Wml::Matrix4<T>& transformM,const Wml::Matrix3<T>& rotM,
				  const Wml::Vector3<T>& translateGM)
{
	int i,j;

	for(i=0;i<3;i++)
		for(j=0;j<3;j++)
			transformM(i,j)=rotM(i,j);

	for(i=0;i<3;i++)
		transformM(i,3)=translateGM[i];

	for(j=0;j<3;j++)
		transformM(3,j)=0.0;

	transformM(3,3)=1.0;

}
#endif