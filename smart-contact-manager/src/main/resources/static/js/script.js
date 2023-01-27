console.log("Hello how are you")

{
	
}

const toggleSidebar =()=>
{
	if($(".sidebar").is(":visible"))
	{
		// if true do close sidebar
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
		
		 
	}
	else
	{
		// if false do open sidebar
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
		
	}
};


