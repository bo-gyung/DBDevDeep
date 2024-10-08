$(document).ready(function() {
	fetch('/api/emp-tree')
		.then(response => response.json())
		.then(data => {
			const D1Data = D1(data.employees, data.teacherHistory);
			const D2Data = D2(data.employees, data.teacherHistory);
			const D3Data = D3(data.employees);
			const D3J4Data = D3J4(data.employees);
			const D4Data = D4(data.employees, data.teacherHistory);
			const D4J4Data = D4J4(data.employees, data.teacherHistory)


			$('#sidebarnav').jstree({
				'core': {
					'data': Object.keys(D1Data).length !== 0
						? D1Data : Object.keys(D2Data).length !== 0
							? D2Data : Object.keys(D3Data).length !== 0
								? [...D3Data, ...D4Data] : Object.keys(D3Data).length !== 0
									? [...D3J4Data, ...D4Data] : Object.keys(D4Data).length !== 0
										? [...D3Data, ...D4J4Data] : Object.keys(D3Data).length !== 0 && Object.keys(D4Data).length !== 0
											? [...D3J4Data, ...D4J4Data] : [],
					"themes": {
						"responsive": false
					}
				},

				"types": {
					"default": {
						"icon": "fas fa-user" // 아이콘 변경
					}
				},
				"plugins": ["types"]

			});


		});

	function getTeacherHistory(empId, teacherHistory) {
		const history = teacherHistory.find(t => t.teach_emp_id === empId);
		return history ? { grade: history.grade, grade_class: history.grade_class } : { grade: null, grade_class: null };
	}

	// 교장
	function D1(items, historys) {
		return items
			.filter(item => item.dept_code == 'D1')
			.map(item => {
				return {
					"text": item.dept_name + " " + item.emp_name,
					"state": { "opened": true },
					"children": D2(items, historys)
				};
			});
	}

	// 교감
	function D2(items, historys) {
		return items
			.filter(item => item.dept_code == 'D2')
			.map(item => {
				return {
					"text": item.dept_name + " " + item.emp_name,
					"state": { "opened": true },
					"children": [...D3(items), ...D4(items, historys)]

				}
			})
	}

	// 행정부장
	function D3(items) {
		return items
			.filter(item => item.dept_code == 'D3' && item.job_code == 'J3')
			.map(item => {
				return {
					"text": item.dept_name + "장 " + item.emp_name,
					"state": { "opened": true },
					"children": D3J4(items)
				}
			})
	}

	// 행정직원
	function D3J4(items) {
		return items
			.filter(item => item.dept_code == 'D3' && item.job_code == 'J4')
			.map(item => {
				return {
					"text": item.emp_name,
					"state": { "opened": false }
				}
			})
			.sort((a, b) => {
				return a.text.localeCompare(b.text); // 이름 순으로 정렬
			});
	}

	// 교무부장
	function D4(items, historys) {
		return items
			.filter(item => item.dept_code == 'D4' && item.job_code == 'J3')
			.map(item => {
				const history = getTeacherHistory(item.emp_id, historys);
				return {
					"text": item.dept_name + "장 " + item.emp_name + " (" + history.grade + "-" + history.grade_class + ")",
					"state": { "opened": true },
					"children": D4J4(items, historys)
				}
			})
	}

	// 교무직원
	function D4J4(items, historys) {
		return items
			.filter(item => item.dept_code == 'D4' && item.job_code == 'J4')
			.map(item => {
				const history = getTeacherHistory(item.emp_id, historys);
				const gradeYN = history.grade != null ? item.emp_name + " (" + history.grade + "-" + history.grade_class + ")" : item.emp_name;
				return {
					"text": gradeYN,
					"state": { "opened": false }
				}
			})
			.sort((a, b) => {
				const matchA = a.text.match(/\((\d+)-(\d+)\)/);
				const matchB = b.text.match(/\((\d+)-(\d+)\)/);

				const gradeA = matchA ? parseInt(matchA[1], 10) : null;
				const gradeB = matchB ? parseInt(matchB[1], 10) : null;
				const classA = matchA ? parseInt(matchA[2], 10) : null;
				const classB = matchB ? parseInt(matchB[2], 10) : null;

				if (gradeA !== null && gradeB !== null) {
					if (gradeA === gradeB) {
						return (classA !== null ? classA : 0) - (classB !== null ? classB : 0); // 학년이 같은 경우 반 순으로 정렬
					} else {
						return gradeA - gradeB; // 학년이 다른 경우 학년 순으로 정렬
					}
				} else if (gradeA !== null) {
					return -1; // 두 값 중 하나가 null인 경우 학년이 있는 값 순으로 정렬
				} else if (gradeB !== null) {
					return 1; // 두 값 중 하나가 null인 경우 학년이 있는 값 순으로 정렬
				} else {
					return a.text.localeCompare(b.text); // 두 값이 null이면 이름 순으로 정렬
				}
			});
	}

});