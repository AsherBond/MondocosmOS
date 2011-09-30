// Matrix.h: interface for the CMatrix class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_MATRIX_H__00A5FA3D_BEDB_4D66_91A5_0926A8AE045E__INCLUDED_)
#define AFX_MATRIX_H__00A5FA3D_BEDB_4D66_91A5_0926A8AE045E__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <math.h>
#include <afx.h>
class CMatrix
{
public:
	double Dot(CMatrix const &other);
	CMatrix();
	CMatrix(int nRows, int nCols);
    CMatrix(int nRows, int nCols, double value[]);
	CMatrix(int nSize);
	CMatrix(int nSize, double value[]);
	CMatrix(const CMatrix& other);
	~CMatrix();
	
	BOOL Init(int nRows, int nCols);
	BOOL MakeUnitMatrix(int nSize);
	BOOL FromString(CString s, const CString& sDelim /*= " "*/, BOOL bLineBreak /*= TRUE*/);
	CString ToString(const CString& sDelim /*= " "*/, BOOL bLineBreak /*= TRUE*/) const;
	void SetData(double value[]);

	CString RowToString(int nRow, const CString& sDelim /*= " "*/) const;
	CString ColToString(int nCol, const CString& sDelim /*= " "*/) const;

	double GetElement(int nRow, int nCol) const;
	int	GetNumColumns() const;
	int	GetNumRows() const;
	double* GetData() const;
	int GetRowVector(int nRow, double* pVector) const;
	int GetColVector(int nCol, double* pVector) const;
	CMatrix& operator=(const CMatrix& other);
	BOOL operator==(const CMatrix& other) const;
	BOOL operator!=(const CMatrix& other) const;
	CMatrix	operator+(const CMatrix& other) const;
	CMatrix	operator-(const CMatrix& other) const;
	CMatrix	operator*(double value) const;
	CMatrix	operator/(double value) const;

	CMatrix	operator*(const CMatrix& other) const;


	BOOL CMul(const CMatrix& AR, const CMatrix& AI, const CMatrix& BR, const CMatrix& BI, CMatrix& CR, CMatrix& CI) const;
	CMatrix Transpose() const;
	BOOL InvertGaussJordan();
	BOOL InvertGaussJordan(CMatrix& mtxImag);
	BOOL InvertSsgj();
	BOOL InvertTrench();
	double DetGauss();
	int RankGauss();
	BOOL DetCholesky(double* dblDet);
	BOOL SplitLU(CMatrix& mtxL, CMatrix& mtxU);
	BOOL SplitQR(CMatrix& mtxQ);
	BOOL SplitUV(CMatrix& mtxU, CMatrix& mtxV, double eps /*= 0.000001*/);
	void ppp(double a[], double e[], double s[], double v[], int m, int n);
	void sss(double fg[2], double cs[2]);
	BOOL GInvertUV(CMatrix& mtxAP, CMatrix& mtxU, CMatrix& mtxV, double eps /*= 0.000001*/);
	BOOL MakeSymTri(CMatrix& mtxQ, CMatrix& mtxT, double dblB[], double dblC[]);
	BOOL SymTriEigenv(double dblB[], double dblC[], CMatrix& mtxQ, int nMaxIt /*= 60*/, double eps /*= 0.000001*/);
	void MakeHberg();
	BOOL HBergEigenv(double dblU[], double dblV[], int nMaxIt /*= 60*/, double eps /*= 0.000001*/);
	BOOL JacobiEigenv(double dblEigenValue[], CMatrix& mtxEigenVector, int nMaxIt = 60, double eps = 0.000001);
	BOOL JacobiEigenv2(double dblEigenValue[], CMatrix& mtxEigenVector, double eps /*= 0.000001*/);
	
	BOOL SetElement(int nRow, int nCol, double value);
	BOOL SetElements(int nStartRow, int nStartCol, int nRow, int nCol, CMatrix value);
	

	int	m_nNumColumns;
	int	m_nNumRows;
	double*	m_pData;
};
#endif // !defined(AFX_MATRIX_H__00A5FA3D_BEDB_4D66_91A5_0926A8AE045E__INCLUDED_)
