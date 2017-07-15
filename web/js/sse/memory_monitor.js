function start_memory_monitor() {
	const eventSource = new EventSource('http://localhost:8080/sse/memory');
	
	const memoryInfo = $('input.memory_info');
	const memoryPercent = $('input.memory_percent');
	
	eventSource.addEventListener('memory_info', function(e) {
		let data = JSON.parse(e.data);
		let info = data.usage + ' / ' + data.total;
		let percent = (data.usage / data.total * 100).toFixed(2);
		
		memoryInfo.val(info);
		memoryPercent.val(percent);
	});
}
