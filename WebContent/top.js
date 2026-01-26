$(function(){
	$('.delete-button').on('click', function(){
		let result = window.confirm('削除しても宜しいですか？');
		if(result == false){
			console.log('キャンセルした');
			event.preventDefault();
		}
	});
});