function [ins,IOB_pred,umaxx,total_daily_unit,insulin_sensitivity_factor]=controller_ins(g_prediction,L,bolus_insulin,basal_insulin,minimum_basal,reference_glucose,Nu,st,body_weight,insulin_sensitivity_constant,flag_constrains)
    g_prediction
    L
    bolus_insulin
    basal_insulin
    minimum_basal
    reference_glucose
    Nu
    st
    insulin_sensitivity_constant
    flag_constrains
    
    
    global umaxx_global
    umaxx_global=basal_insulin(:,end)
    adim=0
    function V=objective(u)
      adim=adim+1
      L
      g_prediction
      u
      Y=L*u
      Y=L*u+g_prediction
      du=diff([basal_insulin(1,end);u])
      total_daily_unit=body_weight*insulin_sensitivity_constant
      insulin_sensitivity_factor=1800./total_daily_unit
      (Y-reference_glucose)'*(Y-reference_glucose)
      basal_insulin(1,end)
      du
      diag(insulin_sensitivity_factor)
      (du'*diag(insulin_sensitivity_factor)*du)
        V=(Y-reference_glucose)'*(Y-reference_glucose)+((du'*diag(insulin_sensitivity_factor)*du))
    end
    function [c, ceq]=constraint(u)
        IOB_pred=zeros(Nu,1);
        for ii=1:Nu
            if ii==1
                IOB_pred(ii,1)=m20150711_calculate_IOB(bolus_insulin,basal_insulin(1,:));
            else
                basal_insulin(1,:)'
                u(1:ii-1)
                IOB_pred(ii,1)=m20150711_calculate_IOB([bolus_insulin;zeros(ii-1,1)],[basal_insulin(1,:)';u(1:ii-1)]);
            end
        end
        total_daily_unit=body_weight*insulin_sensitivity_constant;
        insulin_sensitivity_factor=1800./total_daily_unit;
        umax=((g_prediction(1:Nu)-reference_glucose(1:Nu))./insulin_sensitivity_factor);
        umaxx=max(minimum_basal,min(25,(umax-IOB_pred)*(60/st))); % max of pump 25
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
       insulin_max=35;
        if flag_constrains==1%%
            
            if umaxx(1)*1.5<=insulin_max
                umaxx(1)=umaxx(1)*1.5;
            else
                umaxx(1)=insulin_max;
            end
            if umaxx(1)*1.5<2
                umaxx(1)=2;
            end
        else if flag_constrains==-1
                umaxx(1)=minimum_basal;
            end
        end
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        umin=minimum_basal*ones(Nu,1);
        c=[u-umaxx;umin-u];
        ceq=[];
        umaxx_global=umaxx;
    end

options=optimset('Algorithm','active-set','Display','off');
ins=fmincon(@objective,min(basal_insulin(:,end),umaxx_global),[],[],[],[],[],[],@constraint,options)
ins=round(ins/0.025)*0.025 % 0.025 is due to pump
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%CPA%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%module%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% load controller_limit umaxx_account
% umaxx_account=[umaxx_account,umaxx];
% save controller_limit umaxx umin  umaxx_account L g_prediction
end
