using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace GetGrade
{
    class MessageManager
    {
        /// <summary>
        /// 예외 처리. 에러난 장소와 메세지 보여주기
        /// </summary>
        public static void ShowDialog(string exMessage)
        {
            MessageBox.Show(exMessage);
        }
    }
}
