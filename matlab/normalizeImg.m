function I_norm = normalizeImg(I, M0, VAR0)
M = mean(I(:));
VAR = var(I(:));
I_norm = M0 + sqrt(VAR0/VAR)*abs(I-M);
end
