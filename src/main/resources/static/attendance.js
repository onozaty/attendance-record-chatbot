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

function setChangeActiveEvent(tdElement, tdIndex) {

  const colSelector = 'td:nth-child(' + tdIndex + ')';

  tdElement.addEventListener('mouseover', () => {
    document.querySelectorAll(colSelector).forEach((td) => {
      td.classList.add('active');
    });
  });

  tdElement.addEventListener('mouseleave', () => {
    document.querySelectorAll(colSelector).forEach((td) => {
      td.classList.remove('active');
    });
  });
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

    let tdIndex = 0;

    const dateTdElement = rowElement.appendChild(document.createElement('td'))
    dateTdElement.className = 'date';
    dateTdElement.textContent = dayAttendance.date.slice(-2);
    setChangeActiveEvent(dateTdElement, ++tdIndex);

    allUserNames.forEach(userName => {
      const userComeElement = rowElement.appendChild(document.createElement('td'));
      setChangeActiveEvent(userComeElement, ++tdIndex);

      const userLeaveElement = rowElement.appendChild(document.createElement('td'));
      setChangeActiveEvent(userLeaveElement, ++tdIndex);

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
        {"userName":"user1","comeTime":"09:30:00","leaveTime":"18:00:00"},
        {"userName":"user2","comeTime":"10:00:00","leaveTime":"18:00:00"},
        {"userName":"user3","comeTime":"10:00:00","leaveTime":"19:00:00"}
      ]
    },
    {
      "date":"2020-04-02",
      "users":[
        {"userName":"user1","comeTime":"09:30:30","leaveTime":"19:12:34"},
        {"userName":"user2","comeTime":"13:12:00","leaveTime":"21:11:00"},
        {"userName":"user3","comeTime":"10:00:00","leaveTime":"18:05:00"}
      ]
    },
    {
      "date":"2020-04-03",
      "users":[
        {"userName":"user1","comeTime":"09:25:30","leaveTime":"20:11:22"}
      ]
    },
    {
      "date":"2020-04-04",
      "users":[]
    },
    {
      "date":"2020-04-05",
      "users":[]
    },
    {
      "date":"2020-04-06",
      "users":[
        {"userName":"user1","comeTime":"09:00:00","leaveTime":"15:00:00"},
        {"userName":"user2","comeTime":"08:00:00","leaveTime":null},
        {"userName":"user3","comeTime":"09:58:00","leaveTime":"18:30:00"}
      ]
    },
    {
      "date":"2020-04-07",
      "users":[]
    },
    {
      "date":"2020-04-08",
      "users":[]
    },
    {
      "date":"2020-04-09",
      "users":[]
    },
    {
      "date":"2020-04-10",
      "users":[]
    },
    {
      "date":"2020-04-11",
      "users":[]
    },
    {
      "date":"2020-04-12",
      "users":[]
    },
    {
      "date":"2020-04-13",
      "users":[]
    },
    {
      "date":"2020-04-14",
      "users":[]
    },
    {
      "date":"2020-04-15",
      "users":[]
    },
    {
      "date":"2020-04-16",
      "users":[]
    },
    {
      "date":"2020-04-17",
      "users":[]
    },
    {
      "date":"2020-04-18",
      "users":[]
    },
    {
      "date":"2020-04-19",
      "users":[]
    },
    {
      "date":"2020-04-20",
      "users":[]
    },
    {
      "date":"2020-04-21",
      "users":[]
    },
    {
      "date":"2020-04-22",
      "users":[]
    },
    {
      "date":"2020-04-23",
      "users":[]
    },
    {
      "date":"2020-04-24",
      "users":[]
    },
    {
      "date":"2020-04-25",
      "users":[]
    },
    {
      "date":"2020-04-26",
      "users":[]
    },
    {
      "date":"2020-04-27",
      "users":[]
    },
    {
      "date":"2020-04-28",
      "users":[]
    },
    {
      "date":"2020-04-29",
      "users":[]
    },
    {
      "date":"2020-04-30",
      "users":[]
    }
  ];

  drawAttendances(currentMonth, dummyData);
  */
}

window.addEventListener('hashchange', () => {
  load();
});

load();
