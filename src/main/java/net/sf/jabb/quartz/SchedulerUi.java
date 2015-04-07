/**
 * 
 */
package net.sf.jabb.quartz;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import myschedule.web.ui.MyScheduleUi;

/**
 * Default entry point to the scheduler UI
 * @author James Hu
 *
 */
public class SchedulerUi extends MyScheduleUi {
	private static final long serialVersionUID = -1176128386043039248L;
	
	protected String title;
	
	public SchedulerUi(){
		super();
		this.title = "Quartz Scheduler Manager";
	}

	@Override
    protected void init(VaadinRequest vaadinRequest) {
    	super.init(vaadinRequest);
    	super.getPage().setTitle(this.title);
    	VerticalLayout content = (VerticalLayout)super.getContent();
    	Component headerContent = content.getComponent(0);
    	content.removeComponent(headerContent);
    }

}
