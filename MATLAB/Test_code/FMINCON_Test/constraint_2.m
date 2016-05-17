    function [c, ceq]=constraint_2(Q)
        A=Q(1:3,1)';
        B1=Q(4:15,1)';
        B2=Q(16:19,1)';
        B3=Q(20:23,1)';
        C=Q(24,1)';
        % Converting to State Space
        A_state=[-A B1(2:end) B2(2:end) B3(2:end) C;...
                1 zeros(1,20);...
                zeros(1,1) 1 zeros(1,19);...
                zeros(1,21);...
                zeros(1,3) 1 zeros(1,17);...
                zeros(1,4) 1 zeros(1,16);...
                zeros(1,5) 1 zeros(1,15);...
                zeros(1,6) 1 zeros(1,14);...
                zeros(1,7) 1 zeros(1,13);...
                zeros(1,8) 1 zeros(1,12);...
                zeros(1,9) 1 zeros(1,11);...
                zeros(1,10) 1 zeros(1,10);...
                zeros(1,11) 1 zeros(1,9);...
                zeros(1,12) 1 zeros(1,8);...
                zeros(1,21);...
                zeros(1,14) 1 zeros(1,6);...
                zeros(1,15) 1 zeros(1,5);...
                zeros(1,21);...
                zeros(1,17) 1 zeros(1,3);...
                zeros(1,18) 1 zeros(1,2);...
                zeros(1,21)];
        eigA=abs(eig(A_state));
        c=max(eigA)-0.99;
        ceq = [];
    end