using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Collections;

namespace GetGrade
{
    /// <summary>
    /// 성적이 출력되는 메인 폼
    /// </summary>
    public partial class GradeView : Form
    {
        #region FIELD AREA ******************************************
        GetDataClass dataList;
        #endregion

        #region INITIALIZATION AREA**********************************
        public GradeView()
        {
            InitializeComponent();
            //dataList = new GetDataClass();
        }
        
        private void GradeForm_Load(object sender, EventArgs e)
        {
            AddColumns();
            AddRows();
        }
        #endregion

        #region Method Area(Make ListView) **************************
        /// <summary>
        /// add listview column names
        /// </summary>
        private void AddColumns()
        {
            try
            {
                listView1.Columns.Add("이름", 100);
                foreach (string subjectName in dataList.subjectlist)
                {
                    listView1.Columns.Add(subjectName, 100, HorizontalAlignment.Right);
                }
                listView1.Columns.Add("총점", 100, HorizontalAlignment.Right);
                listView1.Columns.Add("평균", 100, HorizontalAlignment.Right);
                listView1.Columns.Add("석차", 100, HorizontalAlignment.Right);
            }
            catch (Exception ex)
            {
                MessageManager.ShowDialog("add columns : " + ex.Message);
            }
        }
        /// <summary>
        /// add listview row datas
        /// </summary>
        private void AddRows()
        {
            listView1.BeginUpdate();
            ListViewItem row;

            try
            {
                int grade_idx = 0;
                int result_idx = 0;
                foreach (string studentName in dataList.studentlist)
                {
                    row = new ListViewItem(studentName);
                    for (int i = 0; i < dataList.subjectlist.Count; i++)
                    {
                        row.SubItems.Add(dataList.gradelist[grade_idx++].ToString());
                    }
                    for(int i = 0; i < 3; i++)
                    {
                        row.SubItems.Add(dataList.student_Grade_Result_list[result_idx++].ToString());
                    }
                    listView1.Items.Add(row);
                }

                AddSpaceRow();

                row = new ListViewItem("평균");
                for (int i = 0; i < dataList.subjectlist.Count; i++)
                {
                    row.SubItems.Add(dataList.subject_Grade_Result_list[i * 2].ToString());
                }
                listView1.Items.Add(row);

                row = new ListViewItem("표준편차");
                for (int i = 0; i < dataList.subjectlist.Count; i++)
                {
                    row.SubItems.Add(dataList.subject_Grade_Result_list[i * 2 + 1].ToString());
                }
                listView1.Items.Add(row);
                
                listView1.EndUpdate();
            }
            catch (Exception ex)
            {
                MessageManager.ShowDialog("add rows : " + ex.Message);
                Application.Exit();
            }
        }

        /// <summary>
        /// 공백 한 줄
        /// </summary>
        private void AddSpaceRow()
        {
            ListViewItem space = new ListViewItem("");
            listView1.Items.Add(space);
        }
        #endregion
    }
}
