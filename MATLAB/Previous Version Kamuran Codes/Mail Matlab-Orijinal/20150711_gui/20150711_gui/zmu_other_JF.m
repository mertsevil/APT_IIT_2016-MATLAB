%%%%%%%%% zero mean uniunit function with other data Jianyuan Feng 3.6.2015%%%%%%%%%%%%%%%
function Y=zmu_other_JF(mean_other,std_other,X)
[m,n]=size(X);
Y=(X-ones(m,n)*diag(mean_other))/(diag(std_other));
end

