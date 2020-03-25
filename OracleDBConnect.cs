using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using Oracle.DataAccess.Client;

namespace GetGrade
{
    /// <summary>
    /// 오라클 연결 클래스
    /// </summary>
    class OracleDBConnect
    {
        #region CONSTANT AREA *********************************************
        private const string CONNECTION_STRING = "Data Source=orcl; User Id=GRADEUSER; Password=SUCCESS;";
        #endregion

        #region Method Area(Get DB Connection) ****************************
        /// <summary>
        /// 오라클 연결, Data List 리턴
        /// </summary>
        public ArrayList GetDBConnection(string queryString)
        {
            ArrayList list = new ArrayList();

            using (OracleConnection connection = new OracleConnection(CONNECTION_STRING))
            {
                OracleCommand command = new OracleCommand(queryString, connection);
                connection.Open();
                OracleDataReader reader = command.ExecuteReader();
                try
                {
                    while (reader.Read())
                    {
                        for (int i = 0; i < reader.FieldCount; i++)
                        {
                            list.Add(reader.GetValue(i));
                        }
                    }
                }
                catch (Exception ex)
                {
                    MessageManager.ShowDialog("OracleDBConnect : " + ex.Message + "\n *** 성적이 일부만 입력됐습니다.");
                }
                return list;
            }
        }
        #endregion
    }
}
