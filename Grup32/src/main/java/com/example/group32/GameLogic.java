package com.example.group32;

import java.util.HashMap;
import java.util.Map;

public class GameLogic
{
    private final Map<String, String> parent;
    private final Map<String, Integer> rank;

    public GameLogic()
    {
        parent = new HashMap<>();
        rank = new HashMap<>();
    }

    public void makeSetMap(String item)
    {
        parent.put(item, item);
        rank.put(item, 0);

    }

    public String findWinner(String item)
    {
        if (!parent.get(item).equals(item))
        {
            parent.put(item, findWinner(parent.get(item)));
        }
        return parent.get(item);
    }

    public void MapControl(String item1, String item2)
    {
        String root1 = findWinner(item1);
        String root2 = findWinner(item2);

        if (!root1.equals(root2))
        {
            int rank1 = rank.get(root1);
            int rank2 = rank.get(root2);

            if (rank1 > rank2)
            {
                parent.put(root2, root1);
            }
            else if (rank1 < rank2)
            {
                parent.put(root1, root2);
            }
            else
            {
                parent.put(root2, root1);
                rank.put(root1, rank1 + 1);
            }
        }
    }
}
