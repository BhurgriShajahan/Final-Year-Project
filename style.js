var c=45;
console.log(c);

const togle =()=>{
		  if ($(".sidebar").is(":visible"))
		  {
		    $(".sidebar").css("display","none");
		    $(".content").css("margin-left","0%");
		  }
		  else 
		  {
		    $(".sidebar").css("display","block");
		    $(".content").css("margin-left","20%");
		  }
	}
       
       const deleteContact =(id)=>
        {
		
			swal({
			  title: "Are you sure?",
			  text: "You want to delete this!",
			  icon: "warning",
			  buttons: true,
			  dangerMode: true,
			})
			.then((willDelete) => {
			  if (willDelete) {
			    swal("deleted!", {
			      icon: "success",
			    });
			    
			    window.location="/admin/deleteContact/"+id;
			  } else {
			    swal("Data safe");
			  }
			});      
        }
        
        // Delete Teacher
        /* const deleteTeacher =(id)=>
        {
		
			swal({
			  title: "Are you sure?",
			  text: "You want to delete this!",
			  icon: "warning",
			  buttons: true,
			  dangerMode: true,
			})
			.then((willDelete) => {
			  if (willDelete) {
			    swal("deleted!", {
			      icon: "success",
			    });
			    
			    window.location="/admin/deleteTeacher/"+id;
			  } else {
			    swal("Data safe");
			  }
			});      
        }*/
        
        
        // Delete Student
        /* const deleteStudent =(id)=>
        {
		
			swal({
			  title: "Are you sure?",
			  text: "You want to delete this!",
			  icon: "warning",
			  buttons: true,
			  dangerMode: true,
			})
			.then((willDelete) => {
			  if (willDelete) {
			    swal("deleted!", {
			      icon: "success",
			    });
			    
			    window.location="/admin/deleteStudent/"+id;
			  } else {
			    swal("Data safe");
			  }
			});      
        }
            */
        document.addEventListener('DOMContentLoaded', function () {
            const currentPath = window.location.pathname;

            // Add 'active' class to the corresponding sidebar link
            const sidebarLinks = document.querySelectorAll('.sidebar .item');
            sidebarLinks.forEach(function (link) {
                const linkPath = link.getAttribute('href');
                if (currentPath === linkPath) {
                    link.classList.add('active');
                }
            });
        });
        
  	                  