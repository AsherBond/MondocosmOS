// ZOptimizerLM.h: interface for the ZOptimizerLM class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZOPTIMIZERLM_H__7E5301F0_4446_4607_A838_7B4C63A4E0B3__INCLUDED_)
#define AFX_ZOPTIMIZERLM_H__7E5301F0_4446_4607_A838_7B4C63A4E0B3__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZDefOptimizerTerminator.h"
#include "ZAnyScaleFunction.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief Levenberg-Marquardt Optimization for expression.
//////////////////////////////////////////////////////////////////////////
template <class T>
class ZOptimizerLM  
{
public:
	ZOptimizerLM();
	virtual ~ZOptimizerLM();

	int Optimize(ZAnyScaleFunction<T>& Func, Wml::GVector<T>& x, T& MinValue);


public:
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

      void SetVariableNormZero (float zero)
      {
		m_VariableNormZero = zero;
      }

	  void SetLamdaFactor(float lamda)
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

	   /// \brief Pointer to termination condition.
	   ZOptimizerTerminator<T> *m_pTerminator;

	   /// \brief Default termination condition.
	   ZDefOptimizerTerminator<T> m_DefTerminator;
};

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
template <class T>
ZOptimizerLM<T>::ZOptimizerLM()
{
	//////////////////////////////////////////////////////////////////////////
	// NOT used.
	m_FuncValueZero = 100*DBL_EPSILON;
	m_VariableNormZero = 100*DBL_EPSILON;
	//////////////////////////////////////////////////////////////////////////

	m_MaxIteration = 20;

	// 倾斜系数
	m_lamdaFactor = 0.001;

	m_pTerminator = NULL;
}

template <class T>
ZOptimizerLM<T>::~ZOptimizerLM()
{

}

template <class T>
int ZOptimizerLM<T>::Optimize(ZAnyScaleFunction<T>& Func, Wml::GVector<T>& x, T& MinValue)
{
	int i;

	// Number of variables.
	int n = x.GetSize();

	bool	IsReCalculate;

	// Expression values.
	T f;
	T f_2;

	//
	T alamda;

	Wml::GVector<T> grad(n),dir(n),dx(n),mainDiagElement(n);

	Wml::GMatrix<T> hesse(n,n);

	//////////////////////////////////////////////////
	// Set default Terminator
	if( m_pTerminator == NULL ){
		m_pTerminator = &m_DefTerminator;
	}

	// Set all parameters used by terminator!
	// It must be set for everytime running Optimize since
	// all varialbes referred are local variables with this function.
	//m_pTerminator->SetAll( &f, &x, &grad, &dir, &f_2, &alpha );
	m_pTerminator->RegisterParameters(&x, &f, &f_2, &dir);
	//////////////////////////////////////////////////
	//
	f = Func.Value(x);
	alamda = m_lamdaFactor;
	
	IsReCalculate = true;

	//////////////////////////////////////////////////////////////////////////
	T float_zero;

	if (sizeof(T) == 8)
	{
		float_zero = DBL_EPSILON;
	}
	else
	{
		float_zero = FLT_EPSILON;
	}

	//////////////////////////////////////////////////////////////////////////
	int iteration = 0;
	for( iteration=0; iteration< m_MaxIteration; iteration++ )
	{
//		printf("x = %f %f %f %f %f %f\n", x[0], x[1], x[2], x[3], x[4], x[5]);	

		////////////////////////////////////////////////////
		//只有x变化才有必要重新计算！
		if(IsReCalculate)
		{
			Func.Gradient(x, grad);
			Func.Hesse(x, hesse);
		}
		dir = -grad;
		for(i = 0; i < n; i ++)
		{
			mainDiagElement[i] = hesse(i,i);
			//printf("%f\t",mainDiagElement[i]);
			hesse(i,i) = mainDiagElement[i]*(1.0+alamda);
//			if(hesse(i,i)<1e-20)
//				hesse(i,i) += DBL_EPSILON;
		}
		//printf("\n");
		///////////////////////////////////////////////
		// LM算法核心
		///////////////////////////////////////////////
			
		if(!Wml::LinearSystem<T>::SolveSymmetric(hesse,dir,dx))
		{
//			std::cout<<"alamda = "<<alamda<<std::endl;

			alamda *= 10.0;	
			for(i = 0; i < n; i ++)
			{
				if(mainDiagElement[i]<float_zero)
				{

					dx[i] = dir[i]/alamda;
				}
				else
				{
					dx[i] = dir[i]/(mainDiagElement[i]*(1+alamda));
				}
			}
//			for(i=0;i<n;i++)
//				dx[i]=0.0;
		}

		//dx = beta;
		f_2 = Func.Value(x + dx);		
//	

//		printf("f = %f, f_2 = %f \n", f , f_2);		
//		std::cout<<"alamda = "<<alamda<<std::endl;
		//判断是否满足收敛判别准则
		//最后一次要令alamda=0
		if( m_pTerminator->ShouldTerminate() )
		{ 
			goto FINAL;
		}

		if(f_2<f)
		{
			alamda *= 0.1;
			f = f_2;
			x+=dx;			
			IsReCalculate = true;
		}
		else
		{
			alamda *= 10.0;
			f_2=f;
			IsReCalculate = false;
			//复原主对角元素
			for(i=0;i<n;i++)
			{
				hesse(i,i) = mainDiagElement[i];
			}
		}
	}// end of the main iteration
	

	//return false;

FINAL:
	Func.Gradient(x, grad);
	Func.Hesse(x, hesse);
	dir = -grad;

	if(!Wml::LinearSystem<T>::SolveSymmetric(hesse,dir,dx))
	{
		alamda *= 10.0;	
		for(i=0;i<n;i++){
			if(hesse(i,i)<float_zero)
			{
				dx[i] = dir[i]/alamda;
			}
			else
				dx[i] = dir[i]/(hesse(i,i)*(1+alamda));
		}
	}
	
	f_2 = Func.Value(x+dx);
	if(f_2<f){
		f = f_2;
		x+=dx;
	}
	else{
		f_2=f;
	}
	MinValue = f;


//	printf("----------------------iteration = %d ---------------------------\n", iteration);

	return iteration;
}

#endif // !defined(AFX_ZOPTIMIZERLM_H__7E5301F0_4446_4607_A838_7B4C63A4E0B3__INCLUDED_)
