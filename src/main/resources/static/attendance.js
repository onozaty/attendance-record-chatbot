const NBSP = `\u00A0`;

function toMonthString(date) {

  monthString = date.getFullYear() + '-';

  const monthNumber = date.getMonth() + 1;
  if (monthNumber < 10) {
    monthString += '0';
  }
  
  monthString += monthNumber;

  return monthString;
}

function getPageMonth() {

  let pageMonth = location.hash.substring(1);

  if (!pageMonth) {
    pageMonth = toMonthString(new Date());
  }

  return pageMonth;
}

function addMonth(currentMonth, num) {

  const date = new Date(currentMonth);
  date.setMonth(date.getMonth() + num);

  return toMonthString(date);
}

function drawAttendances(currentMonth, attendances) {

  document.getElementById('currentMonth').textContent = currentMonth;
  document.getElementById('prevMonth').href = '#' + addMonth(currentMonth, -1);
  document.getElementById('nextMonth').href = '#' + addMonth(currentMonth, 1)

  const allUserNames = attendances.flatMap(dayAttendance => dayAttendance.users)
        .map(user => user.userName)
        .reduce((userNames, userName) => {
          if (userNames.indexOf(userName) == -1) {
            userNames.push(userName);
          }
          return userNames;
        }, [])
        .sort();

  const tableElement = document.getElementById('attendanceTable');
  while (tableElement.firstChild) {
    tableElement.removeChild(tableElement.firstChild);
  }

  // header
  const headerElement = tableElement.appendChild(document.createElement('thead'))
    .appendChild(document.createElement('tr'));

  const dateThElement = document.createElement('th')
  dateThElement.textContent = NBSP;
  headerElement.appendChild(dateThElement);

  allUserNames.forEach(userName => {
    const userThElement = document.createElement('th');
    userThElement.setAttribute('colspan', '2');
    userThElement.textContent = userName;
    headerElement.appendChild(userThElement);
  });

  // body
  const bodyElement = tableElement.appendChild(document.createElement('tbody'));

  attendances.forEach(dayAttendance => {
    const rowElement = bodyElement.appendChild(document.createElement('tr'));

    const dateTdElement = rowElement.appendChild(document.createElement('td'))
    dateTdElement.className = 'date';
    dateTdElement.textContent = dayAttendance.date.slice(-2);

    allUserNames.forEach(userName => {
      const userComeElement = rowElement.appendChild(document.createElement('td'));
      const userLeaveElement = rowElement.appendChild(document.createElement('td'));

      const user = dayAttendance.users.find(x => x.userName == userName);
      if (user) {
        userComeElement.textContent = (user.comeTime || '').substring(0, 5) || NBSP;
        userLeaveElement.textContent = (user.leaveTime || '').substring(0, 5) || NBSP; 
      } else {
        userComeElement.textContent = NBSP;
        userLeaveElement.textContent = NBSP; 
      }
    });
  });
}

function load() {

  const currentMonth = getPageMonth();

  fetch('./api/attendances?month=' + currentMonth)
    .then(response => response.json())
    .then(attendances => drawAttendances(currentMonth, attendances))
    .catch(error => {
      console.error(error);
    });

  /*
  const dummyData = [
    {
      "date":"2020-04-01",
      "users":[
        {"userName":"user2","comeTime":"10:00:00","leaveTime":"18:00:00"}
      ]
    },
    {
      "date":"2020-04-02",
      "users":[
        {"userName":"user1","comeTime":"09:30:30","leaveTime":"19:12:34"}
      ]
    },
    {
      "date":"2020-04-03",
      "users":[]
    },
    {
      "date":"2020-04-04",
      "users":[
        {"userName":"user1","comeTime":null,"leaveTime":"15:00:00"},
        {"userName":"user2","comeTime":"08:00:00","leaveTime":null}
      ]
    },
    {
      "date":"2020-04-05",
      "users":[
        {"userName":"user3","comeTime":"09:30:30","leaveTime":null}
      ]
    }
  ];

  drawAttendances(currentMonth, dummyData);
  */
}

window.addEventListener('hashchange', () => {
  load();
});

load();
