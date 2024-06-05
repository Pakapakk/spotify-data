SELECT 
    name, 
    artists, 
    popularity
FROM 
    `formatted_spotify_dataset.spotify`
ORDER BY 
    popularity DESC
LIMIT 10;
