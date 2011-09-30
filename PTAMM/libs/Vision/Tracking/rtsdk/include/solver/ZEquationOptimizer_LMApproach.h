// ZEquationOptimizer_LMApproach.h: interface for the ZEquationOptimizer_LMApproach class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZEQUATIONOPTIMIZER_LMAPPROACH_H__B084265F_27D9_4830_81A3_D287F64F0700__INCLUDED_)
#define AFX_ZEQUATIONOPTIMIZER_LMAPPROACH_H__B084265F_27D9_4830_81A3_D287F64F0700__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZDefOptimizerTerminator.h"
#include "ZAnyScaleFunction.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief Levenberg-Marquardt Optimization for expression list. 
//////////////////////////////////////////////////////////////////////////
template <class T>
class ZEquationOptimizer_LMApproach  
{
public:
	typedef std::vector<ZAnyScaleFunction<T>* > ZFunctionList;
	
public:
	ZEquationOptimizer_LMApproach();
	virtual ~ZEquationOptimizer_LMApproach();

 	int Optimize (ZFunctionList * equationlist, Wml::GVector<T>& x, T& MinValue);

	  T Fun(ZFunctionList * equationlist,Wml::GVector<T>& x);

	  void Value(Wml::GVector<T>& value,ZFunctionList* equationlist,Wml::GVector<T>& x);
	  void Jacobbi(Wml::GMatrix<T>& covar,ZFunctionList * equationlist,Wml::GVector<T>&x);

      const ZOptimizerTerminator<T>* GetPtrTerminator () const
      {
		  return m_pTerminator;
      }

      void SetPtrTerminator (const ZOptimizerTerminator<T>* pT)
      {
		  m_pTerminator = const_cast<ZOptimizerTerminator<T>*>(pT);
      }

      int GetMaxIteration () const
      {
		return m_MaxIteration;
      }

       void SetMaxIteration (int MaxIteration)
      {
		m_MaxIteration = MaxIteration;
      }


      T GetVariableNormZero () const
      {
		return m_VariableNormZero;
      }

      void SetVariableNormZero (T zero)
      {
		m_VariableNormZero = zero;
      }

	  void SetLamdaFactor(T lamda)
	  {
		  m_lamdaFactor = lamda;
	  }

	  T GetLamdaFactor()
	  {
		  return m_lamdaFactor;
	  }

protected:
       int m_MaxIteration;
       
	   T m_FuncValueZero;
  
	   T m_VariableNormZero;
  
	   T m_lamdaFactor;

	   ZOptimizerTerminator<T> *m_pTerminator;

	   ZDefOptimizerTerminator<T> m_DefTerminator;

};


//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
template <class T>
ZEquationOptimizer_LMApproach<T>::ZEquationOptimizer_LMApproach()
{
	m_pTerminator = NULL;
	
	// 倾斜系数
	m_lamdaFactor = 0.001;

	m_MaxIteration = 20;

}

template <class T>
ZEquationOptimizer_LMApproach<T>::~ZEquationOptimizer_LMApproach()
{

}

template <class T>
T ZEquationOptimizer_LMApproach<T>::Fun(ZFunctionList * equationlist,Wml::GVector<T>& x)
{
	T value = 0;
	ZFunctionList::iterator	p=equationlist->begin();

	for(;p!=equationlist->end();p++)
	{
		value += pow((*p)->Value(x),2);
	}
	return value;
}

template <class T>
void ZEquationOptimizer_LMApproach<T>::Value(Wml::GVector<T>& value,ZFunctionList* equationlist,Wml::GVector<T>& x)
{
	ZFunctionList::iterator	p=equationlist->begin();

	for(int i=0;p!=equationlist->end();i++,p++){
		value[i] = (*p)->Value(x);
	}
	
}

template <class T>
void ZEquationOptimizer_LMApproach<T>::Jacobbi(Wml::GMatrix<T>& J,ZFunctionList * equationlist,Wml::GVector<T>& x)
{
	int m,n;
	int i,j;
	Wml::GVector<T> grad;
	ZFunctionList::iterator	p=equationlist->begin();

	m = equationlist->size();
	n = x.GetSize();

	J.SetSize(m,n);

	grad.SetSize(n);
	for(i=0;p!=equationlist->end();i++,p++){
		(*p)->Gradient(x,grad);
		for(j=0;j<n;j++){
			J(i,j) = grad[j];
		}
	}
}

template <class T>
int ZEquationOptimizer_LMApproach<T>::Optimize (ZFunctionList * equationlist, Wml::GVector<T>& x, T& MinValue)
{
	int i = 0;
	int m=equationlist->size();
	int n=x.GetSize();
	T f,f_2,alpha;
	T alamda;
	bool IsReCalculate;
	Wml::GVector<T> dir(n),value,mainDiagElement(n);
	Wml::GVector<T> dx(n),grad(n),tmpdir(n);
	
	Wml::GMatrix<T> J,JT,JTJ;
	//////////////////////////////////////////////////
	// Set default Terminator
	if( m_pTerminator == NULL ){
		m_pTerminator = &m_DefTerminator;
	}
	// Set all parameters used by terminator!
	// It must be set for everytime running Optimize since
	// all varialbes referred are local variables with this function.
	m_pTerminator->RegisterParameters(&x,&f,&f_2,&dir);

	//////////////////////////////////////////////////
	//
	f = Fun(equationlist,x);
	alamda = m_lamdaFactor;
	IsReCalculate = true;
	value.SetSize(m);
	int iteration = 0;
	for( iteration=0; iteration< m_MaxIteration; iteration++ )
	{
		////////////////////////////////////////////////////
		//只有x变化才有必要重新计算！
		if(IsReCalculate){			
			Jacobbi(J,equationlist,x);
			JT = J.Transpose();
			JTJ = JT*J;
		}
		Value(value,equationlist,x);
		dir = -JT*value;
		
		for(i=0;i<n;i++)
		{
			mainDiagElement[i] = JTJ(i,i);
			JTJ(i,i)=JTJ(i,i)*(1+alamda);			
		}
		///////////////////////////////////////////////
		// LM算法核心
		///////////////////////////////////////////////
			
		if(!Wml::LinearSystem<T>::SolveSymmetric(JTJ,dir,dx)){
			for(i=0;i<n;i++)
				dx[i]=0.0;
		}

		//dx = beta;
		f_2 = Fun(equationlist,x+dx);
		
		//判断是否满足收敛判别准则
		//最后一次要令alamda=0
		if( m_pTerminator->ShouldTerminate() ){ 
			goto FINAL;
		}

		if(f_2<f){
			alamda *= 0.1;
			f = f_2;
			x+=dx;			
			IsReCalculate = true;
		}
		else{
			alamda *= 10.0;
			f_2=f;
			IsReCalculate = false;
			//复原主对角元素
			for(i=0;i<n;i++){
				JTJ(i,i) = mainDiagElement[i];
			}
		}
	}// end of the main iteration
	

	MinValue = f;
	return false;

FINAL:
	Jacobbi(J,equationlist,x);
	JTJ = J.Transpose()*J;
	Value(value,equationlist,x);
	dir = -JT*value;

	if(!Wml::LinearSystem<T>::SolveSymmetric(JTJ,dir,dx)){
		for(i=0;i<n;i++)
			dx[i]=0.0;
	}
	
	f_2 = Fun(equationlist,x+dx);
	if(f_2<f){
		f = f_2;
		x+=dx;
	}
	else{
		f_2=f;
	}
	MinValue = f;

	return iteration;
}

#endif // !defined(AFX_ZEQUATIONOPTIMIZER_LMAPPROACH_H__B084265F_27D9_4830_81A3_D287F64F0700__INCLUDED_)
