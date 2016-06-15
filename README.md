# NikiRun
仿nike running

bug1:
 navigation drawer点击切换到主页时，发生崩溃，加载homePagefragment失败，出现重复ID; duplicated id with fragment
 
 onCreateView修改成
 if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
		}
		try {
			view = inflater.inflate(R.layout.home_page, container, false);
		} catch (Exception e) {
			// TODO: handle exception
		}
