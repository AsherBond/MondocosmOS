#if !defined(AFX_MODELCLIPPINGDLG_H__E206D99D_646E_11D3_8D0A_00AA00D10994__INCLUDED_)
#define AFX_MODELCLIPPINGDLG_H__E206D99D_646E_11D3_8D0A_00AA00D10994__INCLUDED_

#if _MSC_VER >= 1000
#pragma once
#endif // _MSC_VER >= 1000
// ModelClippingDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CModelClippingDlg dialog
#include "Viewer3dDoc.h"

class CModelClippingDlg : public CDialog
{
// Construction
public:
	CModelClippingDlg(Handle(V3d_View) aView, Handle(V3d_Plane) aPlane, 
			  Handle(AIS_Shape) aShape, CViewer3dDoc* pDoc, CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CModelClippingDlg)
	enum { IDD = IDD_MODELCLIPPING };
	CSliderCtrl	m_ModelClippingZSlidCtrl;
	double	m_ModelClipping_Z;
	BOOL	m_ModelClippingONOFF;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CModelClippingDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CModelClippingDlg)
	afx_msg void OnHScroll(UINT nSBCode, UINT nPos, CScrollBar* pScrollBar);
	virtual BOOL OnInitDialog();
	afx_msg void OnChangeEditModelclippingZ();
	afx_msg void OnCheckModelclippingonoff();
	virtual void OnCancel();
	virtual void OnOK();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()

private:
	Handle_V3d_View myView;
	Handle_AIS_Shape myShape;
	Handle_V3d_Plane myPlane;
	CViewer3dDoc* myDoc;
	double	myModelClipping_Z;
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Developer Studio will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_MODELCLIPPINGDLG_H__E206D99D_646E_11D3_8D0A_00AA00D10994__INCLUDED_)
