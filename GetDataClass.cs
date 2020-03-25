using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace GetGrade
{
    /// <summary>
    /// 성적 데이터를 가져오는 클래스
    /// </summary>
    class GetDataClass
    {
        #region FIELD AREA *************************************
        public ArrayList studentlist = new ArrayList(); //학생정보
        public ArrayList subjectlist = new ArrayList(); //과목정보
        public ArrayList gradelist = new ArrayList(); //점수
        public ArrayList student_Grade_Result_list = new ArrayList(); //총점, 평균, 석차
        public ArrayList subject_Grade_Result_list = new ArrayList(); //평균, 표준편차
        #endregion

        #region INITIALIZATION AREA*****************************
        /// <summary>
        /// 생성자. 리스트 초기화
        /// </summary>
        public GetDataClass()
        {
            getData();
        }
        #endregion

        #region Method Area(Get Data List) *********************
        /// <summary>
        /// SQL로 데이터를 가져옵니다.
        /// </summary>
        private void getData()
        {
            try
            {
                OracleDBConnect dbConnect = new OracleDBConnect();

                string studentNameQuery = "SELECT st.name " +
                                          "FROM STUDENT st " +
                                          "WHERE st.student_id = (SELECT student_id " +
                                                                 "FROM GRADES GR " +
                                                                 "WHERE st.student_id = GR.student_id " +
                                                                 "GROUP BY student_id ) " +
                                          "ORDER BY student_id";

                string subjectNameQuery = "SELECT sb.name " +
                                          "FROM SUBJECT sb " +
                                          "WHERE sb.subject_id = (SELECT subject_id " +
                                                                 "FROM GRADES GR " +
                                                                 "WHERE sb.subject_id = GR.subject_id " +
                                                                 "GROUP BY subject_id ) " +
                                          "ORDER BY sb.subject_id";

                string gradeQuery = "SELECT GRADE " +
                                    "FROM GRADES " +
                                    "ORDER BY student_id, subject_id";

                string student_Grade_Result_Query = "SELECT SUM(grade), AVG(grade), " +
                                              "RANK() OVER (ORDER BY AVG(grade) DESC) RANK " +
                                              "FROM grades " +
                                              "GROUP BY student_id " +
                                              "ORDER BY student_id";

                string subject_Grade_Result_Query = "SELECT AVG(gr.grade), TRUNC(STDDEV(gr.grade),2) " +
                                                    "FROM grades gr " +
                                                    "GROUP BY gr.subject_id " +
                                                    "ORDER BY gr.subject_id";

                studentlist = dbConnect.GetDBConnection(studentNameQuery);
                subjectlist = dbConnect.GetDBConnection(subjectNameQuery);
                gradelist = dbConnect.GetDBConnection(gradeQuery);
                student_Grade_Result_list = dbConnect.GetDBConnection(student_Grade_Result_Query);
                subject_Grade_Result_list = dbConnect.GetDBConnection(subject_Grade_Result_Query);
            }
            catch (Exception ex)
            {
                MessageManager.ShowDialog("GetDataClass : " + ex.Message);
            }
        }
        #endregion
    }
}
