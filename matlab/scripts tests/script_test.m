I = imread('..\data\images\101_1.tif');
[~, I_orient, Gx, Gy] = preprocessingFingerprint(I);

%figure; imshow(mat2gray(I_enhanced)); title('Image améliorée');
%figure; imshow(I_thin); title('Skeleton');
% I_norm_2 = mat2gray(I_norm);
% figure; imshow(uint8(I_norm));


%% Affichage des segments orientés
figure; imshow(I, []); hold on;
[hBlocks, wBlocks] = size(I_orient);
blockSize = 16;

for i = 1:hBlocks
    for j = 1:wBlocks
        theta = I_orient(i,j);

        % centre du bloc
        y = (i-0.5) * blockSize;
        x = (j-0.5)* blockSize;

        % vecteur orienté
        dx = cos(theta)*(blockSize/2);
        dy = sin(theta)*(blockSize/2);

        line([x-dx, x+dx], [y-dy, y+dy], 'Color', 'r', 'LineWidth', 1)
    end
end
title('Orientation field overlay');
hold off;

